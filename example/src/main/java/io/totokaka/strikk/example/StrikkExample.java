package io.totokaka.strikk.example;

import io.totokaka.strikk.annotations.StrikkPlugin;
import io.totokaka.strikk.example.dagger.DaggerComponent;
import io.totokaka.strikk.example.permissions.CommandPermissionsImplementation;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

@StrikkPlugin(
        name = "StrikkExample",
        version = "1.0.0"
)
public class StrikkExample extends JavaPlugin {

    @Inject
    Strikk strikk;

    @Override
    public void onLoad() {
        super.onLoad();

/*
        strikk = new Strikk(
                new CommandPermissionsImplementation(getServer().getPluginManager()),
                new AccessRegistrant(new AccessCommand()),
                new ExampleListenerRegistrant(new ExampleListener()));
*/

        DaggerComponent.builder()
                .bind(this)
                .build()
                .inject(this);

        strikk.register(this);
    }

}
