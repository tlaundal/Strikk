package io.totokaka.strikk.example;

import io.totokaka.strikk.annotations.StrikkPlugin;
import org.bukkit.plugin.java.JavaPlugin;

@StrikkPlugin(
        name = "StrikkExample",
        version = "1.0.0"
)
public class StrikkExample extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();

        getCommand(AccessCommand.NAME).setExecutor(new AccessCommand());
    }
}
