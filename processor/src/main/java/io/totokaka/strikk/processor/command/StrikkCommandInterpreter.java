package io.totokaka.strikk.processor.command;

import io.totokaka.strikk.annotations.StrikkCommand;

import javax.lang.model.element.TypeElement;

public class StrikkCommandInterpreter {

    private final TypeElement element;
    private final StrikkCommand annotation;

    public StrikkCommandInterpreter(TypeElement element, StrikkCommand annotation) {
        this.annotation = annotation;
        this.element = element;
    }

    public String getName() {
        return annotation.name();
    }
}
