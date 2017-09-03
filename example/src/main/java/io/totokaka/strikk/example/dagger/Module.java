package io.totokaka.strikk.example.dagger;

import dagger.Binds;
import dagger.Provides;
import io.totokaka.strikk.example.StrikkExample;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

@dagger.Module
public abstract class Module {

    @Binds
    public abstract Plugin bindsPlugin(StrikkExample strikkExample);

    @Provides
    public static Server provideServer(Plugin plugin) {
        return plugin.getServer();
    }

    @Provides
    public static PluginManager providePluginManager(Server server) {
        return server.getPluginManager();
    }

}
