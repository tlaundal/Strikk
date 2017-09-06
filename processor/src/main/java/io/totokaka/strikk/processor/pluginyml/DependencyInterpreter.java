package io.totokaka.strikk.processor.pluginyml;

import io.totokaka.strikk.annotations.Dependency;
import io.totokaka.strikk.annotations.DependencyType;

public class DependencyInterpreter {

    private final Dependency annotation;

    public DependencyInterpreter(Dependency annotation) {
        this.annotation = annotation;
    }

    public DependencyType getType() {
        return annotation.type();
    }

    public String getName() {
        return annotation.name();
    }

}
