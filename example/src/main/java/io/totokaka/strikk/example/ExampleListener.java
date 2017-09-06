package io.totokaka.strikk.example;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

public class ExampleListener implements Listener {

    @Inject
    public ExampleListener() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("You are joingin a server that runs the StrikkExample plugin!");
    }

    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage("Oh, shaite, forgot to register this listener");
    }

}
