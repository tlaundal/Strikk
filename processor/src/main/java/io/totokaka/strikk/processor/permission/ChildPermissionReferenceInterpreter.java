package io.totokaka.strikk.processor.permission;

import io.totokaka.strikk.annotations.ChildPermissionReference;

import java.lang.annotation.Annotation;

public class ChildPermissionReferenceInterpreter {

    private final StrikkPermissionsInterpreter parent;
    private final ChildPermissionReference annotation;

    public ChildPermissionReferenceInterpreter(StrikkPermissionsInterpreter parent, ChildPermissionReference annotation) {
        this.parent = parent;
        this.annotation = annotation;
    }

    public ChildPermissionReference getFullyResolvedCopy() {
        String name = "";
        if (annotation.value().startsWith(".")) {
            name += parent.getBase();
        }
        name += annotation.value();

        return new ChildPermissionReferenceImplementation(name, annotation.positive());
    }

    private class ChildPermissionReferenceImplementation implements ChildPermissionReference {

        private final String name;
        private final boolean positive;

        private ChildPermissionReferenceImplementation(String name, boolean positive) {
            this.name = name;
            this.positive = positive;
        }

        @Override
        public String value() {
            return name;
        }

        @Override
        public boolean positive() {
            return positive;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return ChildPermissionReference.class;
        }
    }
}
