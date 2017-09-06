package io.totokaka.strikk.processor.command;

import com.squareup.javapoet.*;
import io.totokaka.strikk.annotations.StrikkCommand;
import io.totokaka.strikk.internal.annotations.Registerable;
import io.totokaka.strikk.internal.annotations.RegisteredCommand;
import io.totokaka.strikk.internal.annotations.Registrant;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * A class to generate Registrants for commands
 */
public class CommandRegistrantGenerator {

    private AnnotationSpec registeredCommandAnnotation;
    private FieldSpec commandField;
    private MethodSpec constructor;
    private MethodSpec registerMethod;

    private String commandName;
    private String implementationName;

    public void setOriginalAnnotation(StrikkCommand originalAnnotation) {
        AnnotationSpec original = AnnotationSpec.get(originalAnnotation);
        this.registeredCommandAnnotation = AnnotationSpec.builder(RegisteredCommand.class)
                .addMember("command", "$L", original)
                .build();
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
        this.implementationName = commandName + "Registrant";
    }


    public void setType(TypeElement type) {
        TypeName typeName = TypeName.get(type.asType());

        this.commandField = FieldSpec.builder(typeName, "executor")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        this.registerMethod = MethodSpec.methodBuilder("register")
                .addAnnotation(Override.class)
                .addParameter(JavaPlugin.class, "plugin")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N.getCommand($S).setExecutor($N)", "plugin", commandName, commandField)
                .build();

        ParameterSpec commandParameter = ParameterSpec.builder(typeName, "executor")
                .build();

        this.constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Inject.class)
                .addParameter(commandParameter)
                .addStatement("this.$N = $N", commandField, commandParameter)
                .build();
    }

    public TypeSpec generate() {
        return TypeSpec.classBuilder(implementationName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(Registrant.class)
                .addAnnotation(Registerable.class)
                .addAnnotation(registeredCommandAnnotation)
                .addField(commandField)
                .addMethod(constructor)
                .addMethod(registerMethod)
                .build();
    }
}
