package io.totokaka.strikk.internal.annotations;

import io.totokaka.strikk.annotations.ChildPermissionReference;
import io.totokaka.strikk.annotations.PermissionDefault;
import org.bukkit.permissions.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for internal use.
 *
 * Indicates that this method is an implementation for fetching a {@link Permission}, and that this permission should
 * be registered in the {@code plugin.yml} file.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface RegisteredPermission {

    /**
     * Get the full name of this permission node.
     *
     * @return The full name of this permission node
     */
    String name();

    /**
     * Get the description of this permission node.
     *
     * @return The description of this permission node
     */
    String description() default "";

    /**
     * Get the children of this permission node.
     *
     * The children's names will be treated as absolute.
     * No resolving will be done.
     *
     * @return The children of this permission node
     */
    ChildPermissionReference[] children() default {};

    /**
     * Get the default access for this permission node.
     *
     * @return The default access for this permission node
     */
    PermissionDefault defaultAccess() default PermissionDefault.OP;

}
