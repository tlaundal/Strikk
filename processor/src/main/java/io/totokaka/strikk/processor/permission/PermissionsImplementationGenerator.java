package io.totokaka.strikk.processor.permission;

import com.squareup.javapoet.*;
import io.totokaka.strikk.annotations.ChildPermissionReference;
import io.totokaka.strikk.annotations.PermissionDefault;
import io.totokaka.strikk.internal.annotations.Fetchable;
import io.totokaka.strikk.internal.annotations.RegisteredPermission;
import io.totokaka.strikk.processor.Utils;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PermissionsImplementationGenerator {

    private String implementationName;
    private TypeElement superinterface;
    private AnnotationSpec fetchableAnnotation;
    private FieldSpec pluginManagerField;
    private Set<MethodSpec> implementationMethods;

    public PermissionsImplementationGenerator() {
        this.pluginManagerField = FieldSpec.builder(PluginManager.class, "pluginManager")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL).build();
        this.implementationMethods = new HashSet<>();
    }

    public void setSuperinterface(TypeElement superinterface) {
        this.superinterface = superinterface;
        this.fetchableAnnotation = AnnotationSpec.builder(Fetchable.class)
                .addMember("value", "$T.class", superinterface)
                .build();
    }

    public void setImplementationName(String implementationName) {
        this.implementationName = implementationName;
    }

    public void addChild(String methodName, String name, String description, PermissionDefault defaultAccess,
                         ChildPermissionReference[] children) {
        AnnotationSpec annotation = AnnotationSpec.builder(RegisteredPermission.class)
                .addMember("name", "$S", name)
                .addMember("description", "$S", description)
                .addMember("defaultAccess", "$T.$L", PermissionDefault.class, defaultAccess.name())
                .addMember("children", "$L", Utils.arrayDeclaration(
                        Arrays.stream(children).map(AnnotationSpec::get).toArray()))
                .build();

        MethodSpec implementation = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(annotation)
                .returns(Permission.class)
                .addStatement("return $N.getPermission($S)", pluginManagerField, name)
                .build();

        implementationMethods.add(implementation);
    }

    public TypeSpec generate() {
        ParameterSpec pluginManagerParameter = ParameterSpec.builder(PluginManager.class, "pluginManager").build();
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addAnnotation(Inject.class)
                .addParameter(pluginManagerParameter)
                .addStatement("this.$N = $N", pluginManagerField, pluginManagerParameter)
                .build();

        return TypeSpec.classBuilder(implementationName)
                .addSuperinterface(TypeName.get(superinterface.asType()))
                .addAnnotation(fetchableAnnotation)
                .addField(pluginManagerField)
                .addMethod(constructor)
                .addMethods(implementationMethods)
                .build();
    }
}
