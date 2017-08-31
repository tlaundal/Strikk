package io.totokaka.strikk.annotations;

/**
 * A reference to a permission, as a child of another permission.
 *
 * This takes a lot from {@link PermissionReference}, but also has a
 * {@code boolean positive() default false;} field.
 */
public @interface ChildPermissionReference {

    /**
     * The name of the referenced permission.
     *
     * This should be the method name of the permission in the parent class, or
     * the name as defined by the name field on the {@link StrikkPermission} annotation.
     *
     * @return The name of the referenced permission
     */
    String name();

    /**
     * Whether this is a positive child.
     *
     * A positive child will be set when the parent is set on a permissible.
     * If this is false, this will be a negative child, which means it will be
     * set to false when the parent is set.
     *
     * @return Whether this is a positive child permission
     */
    boolean positive() default true;

    /**
     * The class where the referenced permission is declared.
     *
     * Defaults to the same class as the {@link StrikkPermission} is defined in.
     * This class must be annotated with {@link StrikkPermissions}.
     *
     * @return The class where the referenced permission is declared.
     */
    Class<?> parent() default Void.class;
}
