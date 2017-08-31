package io.totokaka.strikk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates an interface that defines permissions.
 *
 * This annotation should only be applied to interfaces.
 * For each method in the interface that has a {@link StrikkPermission} annotation,
 * a permission will be declared in the plugin.yml
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface StrikkPermissions {

    /**
     * The base path for all permissions defined in this class.
     *
     * @return The base path for all permissions in this class
     */
    String base();

    /**
     * The name of the generated class that will implement this interface.
     *
     * Defaults to the name of the interface suffixed with "Implementation"
     *
     * @return The name of the class that will implement this interface
     */
    String target() default "";

}
