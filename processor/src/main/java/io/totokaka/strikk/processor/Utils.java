package io.totokaka.strikk.processor;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.Collections;

public class Utils {

    private Elements elementUtils;

    public Utils(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    public TypeElement getType(Class<?> clazz) {
        return elementUtils.getTypeElement(clazz.getCanonicalName());
    }

    public boolean isVoid(TypeMirror typeMirror) {
        String test = typeMirror.toString();
        String target = getType(Void.class).asType().toString();
        return test.equals(target);
    }

    public String getPackageName(TypeElement element) {
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }

    /**
     * Creates an array declaration consisting of the objects formatted by JavaPoet as Literals ({@code $L}).
     *
     * @param objects The objects to include in the array declaration
     * @return A CodeBlock with the array declaration
     */
    public static CodeBlock arrayDeclaration(Object... objects) {
        return arrayDeclaration("$L", objects);
    }

    /**
     * Creates an array declaration consisting of the objects formatted by JavaPoet by the format specified in type.
     *
     * @param type The formatting that JavaPoet should use
     * @param objects The objects to include in the array declaration
     * @return A CodeBlock with the array declaration
     */
    public static CodeBlock arrayDeclaration(String type, Object... objects) {
        Iterable<String> parts = Collections.nCopies(objects.length, type);
        String format = String.format("{%s}", String.join(", ", parts));
        return CodeBlock.of(format, objects);
    }
}
