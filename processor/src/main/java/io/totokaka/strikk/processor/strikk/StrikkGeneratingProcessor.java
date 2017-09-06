package io.totokaka.strikk.processor.strikk;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.totokaka.strikk.internal.annotations.Fetchable;
import io.totokaka.strikk.internal.annotations.Registerable;
import io.totokaka.strikk.internal.annotations.Registrant;
import io.totokaka.strikk.processor.StrikkPackage;
import io.totokaka.strikk.processor.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "io.totokaka.strikk.internal.annotations.Fetchable",
        "io.totokaka.strikk.internal.annotations.Registerable"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StrikkGeneratingProcessor extends AbstractProcessor {

    private Utils utils;
    private Types typeUtils;
    private Messager messager;
    private Filer filer;

    private StrikkGenerator generator;
    private boolean generate;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        this.utils = new Utils(processingEnvironment.getElementUtils());
        this.typeUtils = processingEnvironment.getTypeUtils();
        this.messager = processingEnvironment.getMessager();
        this.filer = processingEnvironment.getFiler();

        this.generator = new StrikkGenerator();
        generate = false;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (generate) {
            return false;
        }
        if (set.contains(utils.getType(Fetchable.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(Fetchable.class)) {
                generate = true;
                processFetchable((TypeElement) element);
            }
        }
        if (set.contains(utils.getType(Registerable.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(Registerable.class)) {
                generate = true;
                processRegisterable((TypeElement) element);
            }
        }

        if (StrikkPackage.getPackageName() == null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Did not have any @StrikkPlugin!");
            return true;
        }

        if (generate) {
            try {
                TypeSpec type = generator.generate();
                JavaFile javaFile = JavaFile.builder(StrikkPackage.getPackageName(), type).build();
                javaFile.writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Could not write Strikk class: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return true;
    }

    private void processRegisterable(TypeElement element) {
        if (!typeUtils.isAssignable(element.asType(), utils.getType(Registrant.class).asType())) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@Registerable not implementing Registrant",
                    element);
            return;
        }

        generator.addRegisterable(element);
    }

    private void processFetchable(TypeElement element) {
        Fetchable annotation = element.getAnnotation(Fetchable.class);
        FetchableInterpreter interpreter = new FetchableInterpreter(annotation);

        generator.addFetchable(interpreter.getFetcherName(), interpreter.getType(), element);
    }
}
