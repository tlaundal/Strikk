package io.totokaka.strikk.annotations;

import java.lang.annotation.Annotation;

public class ChildPermissionReferenceImplementation implements ChildPermissionReference {

    private final String name;
    private final boolean positive;

    public ChildPermissionReferenceImplementation(String name, boolean positive) {
        this.name = name;
        this.positive = positive;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean positive() {
        return positive;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
