package io.totokaka.strikk.processor.listener;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.totokaka.strikk.processor.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "org.bukkit.event.EventHandler"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class EventHandlerProcessor extends AbstractProcessor {

    private Utils utils;
    private Messager messager;
    private Types typeUtil;
    private Filer filer;

    private Set<String> processedListeners;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        this.utils = new Utils(processingEnvironment.getElementUtils());
        this.messager = processingEnvironment.getMessager();
        this.typeUtil = processingEnvironment.getTypeUtils();
        this.filer = processingEnvironment.getFiler();

        this.processedListeners = new HashSet<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.contains(utils.getType(EventHandler.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(EventHandler.class)) {
                processElement((ExecutableElement) element);
            }
            return true;
        } else {
            return false;
        }
    }

    private void processElement(ExecutableElement element) {
        if (element.getEnclosingElement() == null || element.getEnclosingElement().getKind() != ElementKind.CLASS) {
            messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "@EventHandler is not in class",
                    element);
            return;
        }
        TypeElement type = (TypeElement) element.getEnclosingElement();

        if (!typeUtil.isAssignable(type.asType(), utils.getType(Listener.class).asType())) {
            messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "@EventHandler is not in Listener",
                    element);
            return;
        }

        String name = type.getQualifiedName().toString();
        if (!processedListeners.contains(name)) {
            processListener(type);
            processedListeners.add(name);
        }
    }

    private void processListener(TypeElement type) {
        ListenerRegistrantGenerator generator = new ListenerRegistrantGenerator();

        generator.setType(type);

        TypeSpec registrant = generator.generate();
        JavaFile javaFile = JavaFile.builder(utils.getPackageName(type), registrant).build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Could not write listener registrant: " + e.getLocalizedMessage(), type);
            e.printStackTrace();
        }
    }

}
