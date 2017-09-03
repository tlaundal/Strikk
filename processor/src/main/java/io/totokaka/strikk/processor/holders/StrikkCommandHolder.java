package io.totokaka.strikk.processor.holders;

import io.totokaka.strikk.annotations.StrikkCommand;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StrikkCommandHolder {

    private final TypeElement type;
    private final StrikkCommand command;

    public StrikkCommandHolder(TypeElement element, StrikkCommand command) {
        this.type = element;
        this.command = command;
    }

    public void dump(Map<String, Object> target) {
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

    public boolean hasKnownPermission(Set<String> knownPermissions) {
        if (command.permission().length() == 0) {
            return true;
        }

        return knownPermissions.contains(command.permission());
    }

    public TypeElement getType() {
        return type;
    }

    public StrikkCommand getCommand() {
        return command;
    }
}
