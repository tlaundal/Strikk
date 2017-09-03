package io.totokaka.strikk.example;

import io.totokaka.strikk.annotations.StrikkCommand;
import io.totokaka.strikk.example.permissions.CommandPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

@StrikkCommand(
        name = AccessCommand.NAME,
        description = "Gives access",
        usage = "/<command>",
        permission = CommandPermissions.BASE + CommandPermissions.ACCESS
)
public class AccessCommand implements CommandExecutor {

    public final static String NAME = "access";

    @Inject
    public AccessCommand() { }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] arguments) {
        sender.sendMessage("So you want access!?");

        return true;
    }

}
