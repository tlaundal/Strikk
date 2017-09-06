package io.totokaka.strikk.processor.pluginyml;

import io.totokaka.strikk.annotations.ChildPermissionReference;
import io.totokaka.strikk.annotations.PermissionDefault;
import io.totokaka.strikk.internal.annotations.RegisteredPermission;

import java.util.HashMap;
import java.util.Map;

public class PermissionInterpreter {

    private final RegisteredPermission annotation;

    public PermissionInterpreter(RegisteredPermission annotation) {
        this.annotation = annotation;
    }

    public String getName() {
        return annotation.name();
    }

    public String getDescription() {
        return annotation.description();
    }

    public PermissionDefault getDefaultAccess() {
        return annotation.defaultAccess();
    }

    public Map<String, Boolean> getChildren() {
        Map<String, Boolean> map = new HashMap<>();
        for (ChildPermissionReference child : annotation.children()) {
            map.put(child.value(), child.positive());
        }
        return map;
    }

}
