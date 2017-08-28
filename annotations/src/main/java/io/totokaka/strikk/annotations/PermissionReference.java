package io.totokaka.strikk.annotations;

/**
 * A reference to a {@link StrikkPermission}.
 *
 * The purpose of this annotation is to make refactoring permissions safer, and
 * introduce some type checking to permission references.
 */
public @interface PermissionReference {

    /**
     * The class where the referenced permission is declared.
     *
     * This class must be annotated with {@link StrikkPermissions}.
     *
     * @return The class where the referenced permission is declared.
     */
    Class<?> parent();

    /**
     * The name of the referenced permission.
     *
     * This should be the method name of the permission in the parent class, or
     * the name as defined by the name field on the {@link StrikkPermission} annotation.
     *
     * @return The name of the referenced permission
     */
    String name();

}
