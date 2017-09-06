package io.totokaka.strikk.processor.strikk;

import io.totokaka.strikk.internal.annotations.Fetchable;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class FetchableInterpreter {
    private final Fetchable annotation;

    public FetchableInterpreter(Fetchable annotation) {
        this.annotation = annotation;
    }

    public String getFetcherName() {
        try {
            return "get" + annotation.value().getSimpleName();
        } catch (MirroredTypeException mirroredTypeException) {
            TypeMirror mirror = mirroredTypeException.getTypeMirror();
            return "get" + mirror.accept(new GetNameVisitor(), null);
        }
    }

    public TypeMirror getType() {
        try {
            annotation.value();
        } catch (MirroredTypeException mirroredTypeException) {
            return mirroredTypeException.getTypeMirror();
        }

        return null;
    }

}
