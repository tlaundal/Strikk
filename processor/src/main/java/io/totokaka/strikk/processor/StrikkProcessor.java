package io.totokaka.strikk.processor;

import com.google.auto.service.AutoService;
import io.totokaka.strikk.annotations.StrikkCommand;
import io.totokaka.strikk.annotations.StrikkPermission;
import io.totokaka.strikk.annotations.StrikkPermissions;
import io.totokaka.strikk.annotations.StrikkPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("io.totokaka.strikk.annotations.*")
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

        writePluginYaml();

        return true;
    }

    private void writePluginYaml() {
        try {
            FileObject pluginYamlFileObject = filer.createResource(StandardLocation.SOURCE_OUTPUT, "",
                    "plugin.yml", relatedElements.toArray(new Element[0]));

            Writer writer = pluginYamlFileObject.openWriter();
            yaml.dump(pluginYaml, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean processStrikkPluginAnnotation(RoundEnvironment roundEnv) {
        Element processedElement = null;
        for (Element element : roundEnv.getElementsAnnotatedWith(StrikkPlugin.class)) {
            if (processedElement != null) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("Found second @StrikkPlugin annotation (first was %s)",
                                processedElement.getSimpleName()),
                        element);
                return true;
            }
            processedElement = element;

            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Non-class element annotated with @StrikkPlugin", element);
                return true;
            }

            TypeMirror javaPluginType = Utils.getType(JavaPlugin.class).asType();
            if (!typeUtil.isSubtype(element.asType(), javaPluginType)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@StrikkPlugin annotated class does not extend JavaPlugin", element);
                return true;
            }

            relatedElements.add(element);

            StrikkPlugin plugin = element.getAnnotation(StrikkPlugin.class);
            new StrikkPluginHolder(plugin, (TypeElement) element).dump(pluginYaml);
        }

        return processedElement != null;
    }

    private void processStrikkPermissionsAnnotation(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(StrikkPermissions.class)) {
            if (element.getKind() != ElementKind.INTERFACE) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Non-interface element annotated with @StrikkPermissions", element);
                return;
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
            StrikkCommandHolder holder = new StrikkCommandHolder(command);
            if (!holder.hasKnownPermission(knownPermissions)) {
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                        "@StrikkCommand references uknown permission", element);
            }
            holder.dump(commands);
        }
    }

}
