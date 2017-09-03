package io.totokaka.strikk.annotations;

/**
 * A reference to a permission, as a child of another permission.
 */
public @interface ChildPermissionReference {

    /**
     * The name of the referenced permission.
     *
     * The name may start with a dot, in which case it is resolved
     * relative to the base path used on the {@code @StrikkPermission}
     * this is a child of.
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

}
