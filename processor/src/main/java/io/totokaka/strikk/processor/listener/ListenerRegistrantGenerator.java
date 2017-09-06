package io.totokaka.strikk.processor.listener;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.totokaka.strikk.internal.ListenerRegistrant;
import io.totokaka.strikk.internal.annotations.Registerable;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class ListenerRegistrantGenerator {

    private MethodSpec constructor;

    private String implementationName;

    public void setType(TypeElement type) {
        implementationName = type.getSimpleName().toString() + "Registrant";
        ParameterSpec parameter = ParameterSpec.builder(TypeName.get(type.asType()), "listener").build();

        this.constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Inject.class)
                .addParameter(parameter)
                .addStatement("super($N)", parameter)
                .build();
    }

    public TypeSpec generate() {
        return TypeSpec.classBuilder(implementationName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ListenerRegistrant.class)
                .addAnnotation(Registerable.class)
                .addMethod(constructor)
                .build();
    }

}
