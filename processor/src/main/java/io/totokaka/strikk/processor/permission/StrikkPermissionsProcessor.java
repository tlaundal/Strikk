package io.totokaka.strikk.processor.permission;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.totokaka.strikk.annotations.StrikkPermission;
import io.totokaka.strikk.annotations.StrikkPermissions;
import io.totokaka.strikk.processor.Utils;
import org.bukkit.permissions.Permission;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "io.totokaka.strikk.annotations.StrikkPermissions",
        "io.totokaka.strikk.annotations.StrikkPermission"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StrikkPermissionsProcessor extends AbstractProcessor {

    private Utils utils;
    private Filer filer;
    private Messager messager;
    private Types typeUtil;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        this.utils = new Utils(processingEnvironment.getElementUtils());
        this.filer = processingEnvironment.getFiler();
        this.messager = processingEnvironment.getMessager();
        this.typeUtil = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        boolean claims = false;

        if (set.contains(utils.getType(StrikkPermission.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(StrikkPermission.class)) {
                verifyPermissionAnnotation((ExecutableElement) element);
            }
            claims = true;
        }

        if (set.contains(utils.getType(StrikkPermissions.class))) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(StrikkPermissions.class)) {
                processPermissionsAnnotation((TypeElement) element);
            }
            claims = true;
        }

        return claims;
    }

    private void processPermissionsAnnotation(TypeElement element) {
        StrikkPermissions annotation = element.getAnnotation(StrikkPermissions.class);
        StrikkPermissionsInterpreter interpreter = new StrikkPermissionsInterpreter(element, annotation);

        PermissionsImplementationGenerator generator = new PermissionsImplementationGenerator();

        generator.setSuperinterface(element);
        generator.setImplementationName(interpreter.getImplementationName());

        for (Element child : element.getEnclosedElements()) {
            StrikkPermission childAnnotation = child.getAnnotation(StrikkPermission.class);
            if (childAnnotation == null) {
                continue;
            }

            StrikkPermissionInterpreter childInterpreter =
                    new StrikkPermissionInterpreter(interpreter, child, childAnnotation);

            generator.addChild(child.getSimpleName().toString(), childInterpreter.getName(), childInterpreter.getDescription(),
                    childInterpreter.getDefaultAccess(), childInterpreter.resolveChildren());
        }

        TypeSpec implementation = generator.generate();
        JavaFile javaFile = JavaFile.builder(utils.getPackageName(element), implementation).build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Could not write permission implementation: " + e.getLocalizedMessage(), element);
            e.printStackTrace();
        }
    }

    private void verifyPermissionAnnotation(ExecutableElement element) {
        TypeMirror returnType = element.getReturnType();
        TypeMirror permissionType = utils.getType(Permission.class).asType();
        if (!typeUtil.isSameType(permissionType, returnType)) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "@StrikkPermission method does not return Permission", element);
            return;
        }
        if (element.getEnclosingElement() == null || element.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@StrikkPermission method is not in interface",
                    element);
            return;
        }
        TypeElement type = (TypeElement) element.getEnclosingElement();

        if (type.getAnnotation(StrikkPermissions.class) == null) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "@StrikkPermission method is not in @StrikkPermissions interface", element);
        }
    }

}
