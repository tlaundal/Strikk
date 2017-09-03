package io.totokaka.strikk.processor.holders;

import io.totokaka.strikk.annotations.StrikkPermissions;

import javax.lang.model.element.TypeElement;

public class StrikkPermissionsHolder {

    private final TypeElement type;
    private final StrikkPermissions annotation;

    public StrikkPermissionsHolder(TypeElement type, StrikkPermissions annotation) {
        this.type = type;
        this.annotation = annotation;
    }

    public String getTarget() {
        if (annotation.target().length() > 0) {
            return annotation.target();
        } else {
            return type.getSimpleName().toString() + "Implementation";
        }
    }
}
