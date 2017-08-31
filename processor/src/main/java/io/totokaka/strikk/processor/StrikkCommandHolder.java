package io.totokaka.strikk.processor;

import io.totokaka.strikk.annotations.StrikkCommand;

import java.util.HashMap;
import java.util.Map;

public class StrikkCommandHolder {

    private final StrikkCommand command;

    public StrikkCommandHolder(StrikkCommand command) {
        this.command = command;
    }

    void dump(Map<String, Object> target) {
        Map<String, Object> commandMap = new HashMap<>();

        if (command.description().length() > 0) {
            commandMap.put("description", command.description());
        }

        if (command.aliases().length > 0) {
            commandMap.put("aliases", command.aliases());
        }

        if (command.permissionMessage().length() > 0) {
            commandMap.put("permission-message", command.permissionMessage());
        }

        if (command.usage().length() > 0) {
            commandMap.put("usage", command.usage());
        }

        if (command.permission().length() > 0) {
            commandMap.put("permission", command.permission());
        }

        target.put(command.name(), commandMap);
    }
}
