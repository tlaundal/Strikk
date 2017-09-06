package io.totokaka.strikk.internal;

import io.totokaka.strikk.internal.annotations.Registrant;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerRegistrant implements Registrant {

    private final Listener listener;

    public ListenerRegistrant(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

}
