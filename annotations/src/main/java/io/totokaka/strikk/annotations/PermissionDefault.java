package io.totokaka.strikk.annotations;

/**
 * Possible default values for a Permission node.
 *
 * Used by the {@link StrikkPermission} annotation.
 */
public enum PermissionDefault {

    /**
     * Indicates this permission defaults to true for all {@link org.bukkit.permissions.Permissible}s.
     */
    TRUE(true),

    /**
     * Indicates this permission defaults to false for all {@link org.bukkit.permissions.Permissible}s.
     */
    FALSE(false),

    /**
     * Indicates this permission defaults to true for all operators.
     */
    OP("op"),

    /**
     * Indicates this permission defaults to true for everyone who is not an operator.
     */
    NOT_OP("not op");

    private final Object value;

    PermissionDefault(Object value) {
        this.value = value;
    }

    /**
     * Get the name Bukkit uses for this permission default value.
     *
     * This value is suitable for use in plugin.yml.
     *
     * @return The name Bukkit uses for this permission default value
     */
    public Object getValue() {
        return value;
    }
}
