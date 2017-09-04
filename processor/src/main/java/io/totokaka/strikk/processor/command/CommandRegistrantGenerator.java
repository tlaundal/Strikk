package io.totokaka.strikk.processor.command;

import com.squareup.javapoet.*;
import io.totokaka.strikk.annotations.StrikkCommand;
import io.totokaka.strikk.processor.Utils;
import io.totokaka.strikk.annotations.internal.Registerable;
import io.totokaka.strikk.annotations.internal.RegisteredCommand;
import io.totokaka.strikk.annotations.internal.Registrant;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * A class to generate Registrants for commands
 */
public class CommandRegistrantGenerator {

    private final Utils utils;

    private AnnotationSpec registeredCommandAnnotation;
    private FieldSpec commandField;
    private ParameterSpec commandParameter;

    private String commandName;
    private String implementationName;

    public CommandRegistrantGenerator(Utils utils) {
        this.utils = utils;
    }

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
        this.commandParameter = ParameterSpec.builder(typeName, "executor")
                .build();
    }

    public TypeSpec generate() {
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addAnnotation(Inject.class)
                .addParameter(commandParameter)
                .addStatement("this.$N = $N", commandField, commandParameter)
                .build();

        MethodSpec registerMethod = MethodSpec.methodBuilder("register")
                .addAnnotation(Override.class)
                .addParameter(JavaPlugin.class, "plugin")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N.getCommand($S).setExecutor($N)", "plugin", commandName, commandField)
                .build();

        return TypeSpec.classBuilder(implementationName)
                .addSuperinterface(Registrant.class)
                .addAnnotation(Registerable.class)
                .addAnnotation(registeredCommandAnnotation)
                .addField(commandField)
                .addMethod(constructor)
                .addMethod(registerMethod)
                .build();
    }
}
