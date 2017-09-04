package io.totokaka.strikk.internal.annotations;

import io.totokaka.strikk.annotations.StrikkCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for internal use.
 *
 * Indicates that this class has to do with a registered command, and that this command should
 * be registered in the {@code plugin.yml} file.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface RegisteredCommand {

    /**
     * Get the command that is registered.
     *
     * @return The command
     */
    StrikkCommand command();

}
