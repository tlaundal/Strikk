package io.totokaka.strikk.example;

import io.totokaka.strikk.annotations.StrikkCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@StrikkCommand(
        name = AccessCommand.NAME,
        description = "Gives access",
        usage = "/<command>",
        permission = IPermissions.BASE + "." + IPermissions.ACCESS
)
public class AccessCommand implements CommandExecutor {

    public final static String NAME = "access";

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] arguments) {
        sender.sendMessage("So you want access!?");

        return true;
    }

}
