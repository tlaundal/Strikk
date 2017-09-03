package io.totokaka.strikk.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import io.totokaka.strikk.annotations.StrikkCommand;
import io.totokaka.strikk.annotations.StrikkPermission;
import io.totokaka.strikk.annotations.StrikkPermissions;
import io.totokaka.strikk.annotations.StrikkPlugin;
import io.totokaka.strikk.processor.holders.StrikkCommandHolder;
import io.totokaka.strikk.processor.holders.StrikkPermissionHolder;
import io.totokaka.strikk.processor.holders.StrikkPermissionsHolder;
import io.totokaka.strikk.processor.holders.StrikkPluginHolder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.*;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({
        "io.totokaka.strikk.annotations.*"
})
@AutoService(Processor.class)
public class StrikkProcessor extends AbstractProcessor {

    private Messager messager;
    private Types typeUtil;
    private Filer filer;

    private Yaml yaml;
    private Map<String, Object> pluginYaml;
    private Set<Element> relatedElements;

    private Set<String> knownPermissions;
    private Map<String, Element> referencedChildPermissions;

    private TypeElement plugin;
    private Map<TypeElement, JavaFile> permissionImplementations;
    private Set<StrikkCommandHolder> commands;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        messager = processingEnvironment.getMessager();
        typeUtil = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();

        Utils.setElementUtils(processingEnvironment.getElementUtils());

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
        pluginYaml = new HashMap<>();
        relatedElements = new HashSet<>();

        knownPermissions = new HashSet<>();
        referencedChildPermissions = new HashMap<>();

        permissionImplementations = new HashMap<>();
        commands = new HashSet<>();
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean claims = processStrikkPluginAnnotation(roundEnv);
        if (!claims) {
            messager.printMessage(Diagnostic.Kind.WARNING, "Did not process any @StrikkPlugin annotations");
            return false;
        }

        processStrikkPermissionsAnnotation(roundEnv);
        processStrikkPermissionAnnotation(roundEnv);
        processStrikkCommandAnnotation(roundEnv);

        try {
            writePermissionsImplementation(roundEnv);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Could not write permission implementation: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (roundEnv.processingOver()) {
            try {
                writePluginYaml();
                writeStrikk();
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Could not write file: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        return true;
    }

    private void writeStrikk() throws IOException {
        Set<FieldSpec> fields = new HashSet<>();
        Set<MethodSpec> methods = new HashSet<>();

        MethodSpec.Builder injectedConstructorBuilder = MethodSpec.constructorBuilder()
                .addAnnotation(Inject.class);

        ParameterSpec pluginManagerParam = ParameterSpec.builder(PluginManager.class, "pluginManager").build();
        MethodSpec.Builder plainConstructorBuilder = MethodSpec.constructorBuilder()
                .addParameter(pluginManagerParam);

        for (Map.Entry<TypeElement, JavaFile> entry : permissionImplementations.entrySet()) {
            JavaFile javaFile = entry.getValue();
            String name = entry.getKey().getSimpleName().toString();
            ClassName typeName = ClassName.get(javaFile.packageName, javaFile.typeSpec.name);

            FieldSpec field = FieldSpec.builder(typeName, name)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            fields.add(field);

            MethodSpec method = MethodSpec.methodBuilder("get" + name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(entry.getKey().asType()))
                    .addStatement("return this.$N", field)
                    .build();
            methods.add(method);

            ParameterSpec parameter = ParameterSpec.builder(typeName, name).build();
            injectedConstructorBuilder.addParameter(parameter)
                    .addStatement("this.$N = $N", field, parameter);

            plainConstructorBuilder.addStatement("this.$N = new $T($N)", field, typeName, pluginManagerParam);
        }

        TypeSpec strikk = TypeSpec.classBuilder("Strikk")
                .addModifiers(Modifier.PUBLIC)
                .addFields(fields)
                .addMethod(injectedConstructorBuilder.build())
                .addMethod(plainConstructorBuilder.build())
                .addMethods(methods).build();

        PackageElement parent = (PackageElement) plugin.getEnclosingElement();
        JavaFile javaFile = JavaFile.builder(parent.getQualifiedName().toString(), strikk).build();

        FileObject fileObject = filer.createSourceFile(javaFile.packageName + "." + strikk.name);
        Writer writer = fileObject.openWriter();
        javaFile.writeTo(writer);
        writer.close();
    }

    private void writePermissionsImplementation(RoundEnvironment roundEnv) throws IOException {
        for (Element element : roundEnv.getElementsAnnotatedWith(StrikkPermissions.class)) {
            TypeElement type = (TypeElement) element;
            PackageElement packageElement = (PackageElement) type.getEnclosingElement();
            StrikkPermissions parent = element.getAnnotation(StrikkPermissions.class);
            StrikkPermissionsHolder parentHolder = new StrikkPermissionsHolder(type, parent);

            FieldSpec pluginManager = FieldSpec.builder(PluginManager.class, "pluginManager")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();

            Set<MethodSpec> methods = new HashSet<>();
            for (Element child : element.getEnclosedElements()) {
                if (child.getKind() != ElementKind.METHOD) {
                    continue;
                }
                ExecutableElement executable = (ExecutableElement) child;
                StrikkPermission permission = child.getAnnotation(StrikkPermission.class);
                StrikkPermissionHolder holder = new StrikkPermissionHolder(parent, permission,
                        (ExecutableElement) child);

                methods.add(MethodSpec.methodBuilder(executable.getSimpleName().toString())
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Permission.class)
                        .addStatement("return $N.getPermission($S)", pluginManager, holder.resolveName())
                        .build());
            }

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addAnnotation(Inject.class)
                    .addParameter(PluginManager.class, "pluginManager")
                    .addStatement("this.$N = $N", pluginManager, "pluginManager")
                    .build();

            TypeSpec implementation = TypeSpec.classBuilder(parentHolder.getTarget())
                    .addSuperinterface(TypeName.get(type.asType()))
                    .addModifiers(Modifier.PUBLIC)
                    .addField(pluginManager)
                    .addMethod(constructor)
                    .addMethods(methods)
                    .build();

            JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), implementation).build();
            permissionImplementations.put(type, javaFile);
            FileObject fileObject = filer.createSourceFile(javaFile.packageName + "." + implementation.name);
            Writer writer = fileObject.openWriter();
            javaFile.writeTo(writer);
            writer.close();
        }
    }

    private void writePluginYaml() throws IOException {
        FileObject pluginYamlFileObject = filer.createResource(StandardLocation.SOURCE_OUTPUT, "",
                "plugin.yml", relatedElements.toArray(new Element[0]));

        Writer writer = pluginYamlFileObject.openWriter();
        yaml.dump(pluginYaml, writer);
        writer.close();
    }

    private boolean processStrikkPluginAnnotation(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(StrikkPlugin.class)) {
            if (plugin != null) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("Found second @StrikkPlugin annotation (first was %s)",
                                plugin.getSimpleName()),
                        element);
                return true;
            }

            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Non-class element annotated with @StrikkPlugin", element);
                return true;
            }

            plugin = (TypeElement) element;

            TypeMirror javaPluginType = Utils.getType(JavaPlugin.class).asType();
            if (!typeUtil.isAssignable(element.asType(), javaPluginType)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@StrikkPlugin annotated class does not extend JavaPlugin", element);
                return true;
            }

            relatedElements.add(element);

            StrikkPlugin plugin = element.getAnnotation(StrikkPlugin.class);
            new StrikkPluginHolder(plugin, (TypeElement) element).dump(pluginYaml);
        }

        return plugin != null;
    }

    private void processStrikkPermissionsAnnotation(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(StrikkPermissions.class)) {
            Element parent = element.getEnclosingElement();
            if (parent == null) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@StrikkPermissions declared on element without enclosing element",
                        element);
                return;
            }
            if (parent.getKind() != ElementKind.PACKAGE) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@StrikkPermissions declared on element with enclosing element that is not a " +
                                "package",
                        parent);
                return;
            }

            if (element.getKind() != ElementKind.INTERFACE) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Non-interface element annotated with @StrikkPermissions", element);
                return;
            }

            for (Element child : element.getEnclosedElements()) {
                if (child.getKind() != ElementKind.METHOD && child.getKind() != ElementKind.FIELD) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "@StrikkPermissions annotated interface contains element witch is not a field or a method", child);
                    return;
                }

                if (child.getKind() == ElementKind.METHOD) {
                    ExecutableElement executable = (ExecutableElement) child;
                    if (!executable.isDefault() && executable.getAnnotation(StrikkPermission.class) == null) {
                        messager.printMessage(Diagnostic.Kind.ERROR,
                                "@StrikkPermissions annotated interface contains non-defuault method without " +
                                        "@StrikkPermission annotation.",
                                executable);
                        return;
                    }
                }
            }
        }
    }

    private void processStrikkPermissionAnnotation(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(StrikkPermission.class)) {
            if (element.getKind() != ElementKind.METHOD) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Non-method element annotated with @StrikkPermission", element);
                return;
            }

            ExecutableElement method = (ExecutableElement) element;
            Element parentElement = method.getEnclosingElement();
            if (parentElement.getAnnotation(StrikkPermissions.class) == null) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@StrikkPermission used on method enclosed in element missing the @StrikkPermission annotation",
                        element);
                return;
            }

            StrikkPermissions parent = parentElement.getAnnotation(StrikkPermissions.class);
            StrikkPermission permission = element.getAnnotation(StrikkPermission.class);

            Map<String, Object> permissions;
            if (pluginYaml.containsKey("permissions")) {
                permissions = (Map<String, Object>) pluginYaml.get("permissions");
            } else {
                permissions = new HashMap<>();
                pluginYaml.put("permissions", permissions);
            }

            StrikkPermissionHolder holder = new StrikkPermissionHolder(parent, permission, method);
            for (String child : holder.children()) {
                referencedChildPermissions.put(child, element);
            }
            holder.dump(permissions);
        }
        knownPermissions = ((Map<String, Object>)pluginYaml.getOrDefault("permissions", new HashMap())).keySet();

        for (Map.Entry<String, Element> reference : referencedChildPermissions.entrySet()) {
            if (!knownPermissions.contains(reference.getKey())) {
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                        "@ChildPermission referenced uknown permission", reference.getValue());
            }
        }
    }

    private void processStrikkCommandAnnotation(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(StrikkCommand.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Non-class element annotated with @StrikkCommand", element);
                return;
            }

            TypeMirror commandExecutorType = Utils.getType(CommandExecutor.class).asType();
            if (!typeUtil.isAssignable(element.asType(), commandExecutorType)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@StrikkCommand annotated class does not implement CommandExecutor", element);
                return;
            }

            Map<String, Object> commands;
            if (pluginYaml.containsKey("commands")) {
                commands = (Map<String, Object>) pluginYaml.get("commands");
            } else {
                commands = new HashMap<String, Object>();
                pluginYaml.put("commands", commands);
            }

            StrikkCommand command = element.getAnnotation(StrikkCommand.class);
            StrikkCommandHolder holder = new StrikkCommandHolder((TypeElement) element, command);
            if (!holder.hasKnownPermission(knownPermissions)) {
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                        "@StrikkCommand references uknown permission", element);
            }

            this.commands.add(holder);
            holder.dump(commands);
        }
    }

}
