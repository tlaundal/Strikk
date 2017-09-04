package io.totokaka.strikk.processor.permission;

import io.totokaka.strikk.annotations.StrikkPermissions;

import javax.lang.model.element.TypeElement;

public class StrikkPermissionsInterpreter {

    private final TypeElement type;
    private final StrikkPermissions annotation;

    public StrikkPermissionsInterpreter(TypeElement type, StrikkPermissions annotation) {
        this.type = type;
        this.annotation = annotation;
    }

    public String getImplementationName() {
        if (annotation.target().length() > 0) {
            return annotation.target();
        } else {
            return type.getSimpleName().toString() + "Implementation";
        }
    }

    public String getBase() {
        return annotation.base();
    }
}
