package io.totokaka.strikk.annotations.internal;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * An interface to mark something which registers itself with a JavaPlugin
 */
public interface Registrant {

    /**
     * Register something with the given plugin.
     *
     * @param plugin The plugin to register with
     */
    void register(JavaPlugin plugin);

}
