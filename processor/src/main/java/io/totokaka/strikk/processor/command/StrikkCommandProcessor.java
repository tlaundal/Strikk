package io.totokaka.strikk.processor.command;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.totokaka.strikk.annotations.StrikkCommand;
import io.totokaka.strikk.processor.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "io.totokaka.strikk.annotations.StrikkCommand"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StrikkCommandProcessor extends AbstractProcessor {

    private Utils utils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        this.utils = new Utils(processingEnvironment.getElementUtils());
        this.filer = processingEnvironment.getFiler();
        this.messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.contains(utils.getType(StrikkCommand.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(StrikkCommand.class)) {
                processElement((TypeElement) element);
            }
            return true;
        } else {
            return false;
        }
    }

    private void processElement(TypeElement element) {
        StrikkCommand annotation = element.getAnnotation(StrikkCommand.class);
        StrikkCommandInterpreter interpreter = new StrikkCommandInterpreter(element, annotation);

        CommandRegistrantGenerator generator = new CommandRegistrantGenerator();

        generator.setOriginalAnnotation(annotation);
        generator.setCommandName(interpreter.getName());
        generator.setType(element);

        TypeSpec registrant = generator.generate();
        JavaFile javaFile = JavaFile.builder(utils.getPackageName(element), registrant).build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Could not write command registrant: " + e.getLocalizedMessage(), element);
            e.printStackTrace();
        }
    }

}
