package io.totokaka.strikk.processor.strikk;

import com.squareup.javapoet.*;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class StrikkGenerator {

    private final MethodSpec.Builder constructor;
    private final ParameterSpec javaPluginParameter;
    private final MethodSpec.Builder registerMethod;
    private final List<FieldSpec> fields;
    private final List<MethodSpec> methods;

    public StrikkGenerator() {
        this.constructor = MethodSpec.constructorBuilder()
                .addAnnotation(Inject.class);

        this.javaPluginParameter = ParameterSpec.builder(JavaPlugin.class, "plugin").build();
        this.registerMethod = MethodSpec.methodBuilder("register")
                .addParameter(javaPluginParameter);

        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public void addRegisterable(TypeElement element) {
        TypeName type = TypeName.get(element.asType());
        String name = element.getSimpleName().toString();

        FieldSpec field = FieldSpec.builder(type, name)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        fields.add(field);

        ParameterSpec param = ParameterSpec.builder(type, name).build();

        constructor.addParameter(param)
                .addStatement("this.$N = $N", field, param);

        registerMethod.addStatement("this.$N.register($N)", field, javaPluginParameter);
    }

    public void addFetchable(String fetcherName, TypeMirror returnType, TypeElement callType) {
        String name = returnType.accept(new GetNameVisitor(), null);
        TypeName type = TypeName.get(returnType);

        FieldSpec field = FieldSpec.builder(type, name)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        fields.add(field);

        ParameterSpec param = ParameterSpec.builder(TypeName.get(callType.asType()), name).build();

        constructor.addParameter(param)
                .addStatement("this.$N = $N", field, param);

        MethodSpec method = MethodSpec.methodBuilder(fetcherName)
                .returns(type)
                .addStatement("return this.$N", field)
                .build();
        methods.add(method);
    }

    public TypeSpec generate() {
        return TypeSpec.classBuilder("Strikk")
                .addAnnotation(Singleton.class)
                .addMethod(constructor.build())
                .addFields(fields)
                .addMethod(registerMethod.build())
                .addMethods(methods)
                .build();
    }
}
