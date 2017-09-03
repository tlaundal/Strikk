package io.totokaka.strikk.annotations;

import java.lang.annotation.Annotation;

public class StrikkPermissionsImplementation implements StrikkPermissions {

    private final String base;
    private final String target;

    public StrikkPermissionsImplementation(String base, String target) {
        this.base = base;
        this.target = target;
    }

    @Override
    public String base() {
        return base;
    }

    @Override
    public String target() {
        return target;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
