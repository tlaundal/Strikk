package io.totokaka.strikk.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

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
}
