package io.totokaka.strikk.annotations;

/**
 * Times a plugin may be loaded during startup.
 */
public enum LoadTime {

    /**
     * Loads a plugin before the worlds are loaded.
     *
     * This is called "STARTUP" by Bukkit.
     */
    PRE_WORLD("STARTUP"),

    /**
     * Loads a plugin after the worlds are loaded.
     *
     * This is the default value for Bukkit.
     */
    POST_WORLD("POSTWORLD");

    private final String value;

    LoadTime(String value) {
        this.value = value;
    }

    /**
     * Get the name Bukkit uses for this load time.
     *
     * This name is suitable for use in a plugin.yml
     *
     * @return The name Bukkit uses for this load time
     */
    public String getValue() {
        return value;
    }
}
