package io.totokaka.strikk.annotations;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A permission declaration.
 *
 * This annotation should only be applied to methods that return a {@link Permission}
 * and takes no arguments, in an interface annotated with {@link StrikkPermissions}.
 * All fields with this annotation will have the permission declared in the generated
 * plugin.yml.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface StrikkPermission {

    /**
     * The name of this permission.
     *
     * By default the name of the annotated method will be used. The base defined in
     * the {@link StrikkPermissions} will be added before this name to create the node path.
     *
     * Any leading dots will be removed.
     *
     * @return The name of this permission
     */
    String name() default "";

    /**
     * A short, human readable description of what this permission node is used for.
     *
     * @return A description of this permission
     */
    String description() default "";

    /**
     * Child permissions of this permission.
     *
     * @return Child permissions of this permission
     */
    ChildPermissionReference[] children() default {};

    /**
     * The default value for this permission.
     *
     * This decides the default value of this permission node on {@link Permissible}s that do not
     * have this permission defined.
     *
     * @return The default access for this permission
     */
    PermissionDefault defaultAccess() default PermissionDefault.OP;

}
