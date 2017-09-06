package io.totokaka.strikk.processor.pluginyml;

import com.google.auto.service.AutoService;
import io.totokaka.strikk.annotations.Dependency;
import io.totokaka.strikk.annotations.StrikkPlugin;
import io.totokaka.strikk.internal.annotations.RegisteredCommand;
import io.totokaka.strikk.internal.annotations.RegisteredPermission;
import io.totokaka.strikk.processor.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "io.totokaka.strikk.internal.annotations.RegisteredPermission",
        "io.totokaka.strikk.internal.annotations.RegisteredCommand",
        "io.totokaka.strikk.annotations.StrikkPlugin"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class PluginYmlGeneratingProcessor extends AbstractProcessor {

    private Filer filer;
    private Utils utils;
    private Messager messager;

    private PluginYmlGenerator generator;

    private TypeElement processedPlugin;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        this.filer = processingEnvironment.getFiler();
        this.utils = new Utils(processingEnvironment.getElementUtils());
        this.messager = processingEnvironment.getMessager();

        this.generator = new PluginYmlGenerator();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.contains(utils.getType(StrikkPlugin.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(StrikkPlugin.class)) {
                processPlugin((TypeElement)element);
            }
        }
        if (set.contains(utils.getType(RegisteredPermission.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(RegisteredPermission.class)) {
                processPermission(element.getAnnotation(RegisteredPermission.class));
            }
        }
        if (set.contains(utils.getType(RegisteredCommand.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(RegisteredCommand.class)) {
                processCommand(element.getAnnotation(RegisteredCommand.class));
            }
        }

        if (roundEnvironment.processingOver()) {
            try {
                FileObject fileObject = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "plugin.yml");
                try (Writer writer = fileObject.openWriter()) {
                    generator.generate(writer);
                }
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Could not write plugin.yml: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        return true;
    }

    private void processCommand(RegisteredCommand annotation) {
        CommandInterpreter interpreter = new CommandInterpreter(annotation);
        generator.addCommand(interpreter.getName(), interpreter.getDescription(), interpreter.getAliases(), interpreter.getPermission(), interpreter.getPermissionMessage(), interpreter.getUsage());
    }

    private void processPermission(RegisteredPermission annotation) {
        PermissionInterpreter interpreter = new PermissionInterpreter(annotation);
        generator.addPermission(interpreter.getName(), interpreter.getDescription(), interpreter.getDefaultAccess(),
                interpreter.getChildren());
    }

    private void processPlugin(TypeElement element) {
        if (processedPlugin != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Got second @StrikkPlugin (first was %s)", processedPlugin),
                    element);
            return;
        }
        processedPlugin = element;

        // TODO - verify element is assignable from JavaPlugin

        StrikkPlugin annotation = element.getAnnotation(StrikkPlugin.class);
        StrikkPluginInterpreter interpreter = new StrikkPluginInterpreter(element, annotation);
        generator.setAuthor(interpreter.getAuthor());
        generator.setAuthors(interpreter.getAuthors());
        generator.setDescription(interpreter.getDescription());
        generator.setLoadTime(interpreter.getLoadTime());
        generator.setMain(interpreter.getMain());
        generator.setName(interpreter.getName());
        generator.setPrefix(interpreter.getPrefix());
        generator.setVersion(interpreter.getVersion());
        generator.setWebsite(interpreter.getWebsite());
        generator.setUsesDatabase(interpreter.usesDatabase());
        for (Dependency dependency : interpreter.getDependencies()) {
            DependencyInterpreter depInterpreter = new DependencyInterpreter(dependency);
            generator.addDependency(depInterpreter.getType(), depInterpreter.getName());
        }
    }

}
