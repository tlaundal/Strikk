package io.totokaka.strikk.processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class Utils {

    private static Elements elementUtils;

    static TypeElement getType(Class<?> clazz) {
        return elementUtils.getTypeElement(clazz.getCanonicalName());
    }

    public static void setElementUtils(Elements elementUtils) {
        Utils.elementUtils = elementUtils;
    }

    public static boolean isVoid(TypeMirror typeMirror) {
        String test = typeMirror.toString();
        String target = Utils.getType(Void.class).asType().toString();
        return test.equals(target);
    }
}
