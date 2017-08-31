package io.totokaka.strikk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A command definition.
 *
 * This annotation should only be applied to classes implementing {@link org.bukkit.command.CommandExecutor}.
 * All CommandExecutors annotated with this will have their commands declared in the generated plugin.yml file.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface StrikkCommand {

    /**
     * The name of this command.
     *
     * This will be the command name, users will need to type /&lt name &gt to invoke this command
     *
     * @return The command name
     */
    String name();

    /**
     * A sort, human readable description of this command.
     *
     * Will often be used in /help commands.
     *
     * @return a description of this command
     */
    String description() default "";

    /**
     * Aliases for this command.
     *
     * Alternate names that may be used to call this command.
     *
     * @return Aliases for this command
     */
    String[] aliases() default {};

    /**
     * The message to send to players with insufficient permissions to run this command.
     *
     * @return The message to display when this command is invoked with insufficient permissions.
     */
    String permissionMessage() default "";

    /**
     * A breif description of how this command should be invoked.
     *
     * The macro "&lt;command&gt;" will be replaced with the command name.
     * This message will be printed when the {@link org.bukkit.command.CommandExecutor} returns false.
     *
     * @return A brief description of how this command should be invoked
     */
    String usage() default "";

    /**
     * The permission node required to invoke this command.
     *
     * @return The permission node required to invoke this command
     */
    String permission() default "";

}
