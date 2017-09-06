package io.totokaka.strikk.processor.pluginyml;

import io.totokaka.strikk.internal.annotations.RegisteredCommand;

public class CommandInterpreter {

    private final RegisteredCommand annotation;

    public CommandInterpreter(RegisteredCommand annotation) {
        this.annotation = annotation;
    }

    public String getName() {
        return annotation.command().name();
    }

    public String getDescription() {
        return annotation.command().description();
    }

    public String[] getAliases() {
        return annotation.command().aliases();
    }

    public String getPermission() {
        return annotation.command().permission();
    }

    public String getPermissionMessage() {
        return annotation.command().permissionMessage();
    }

    public String getUsage() {
        return annotation.command().usage();
    }
}
