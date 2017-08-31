package io.totokaka.strikk.processor;

import io.totokaka.strikk.annotations.ChildPermissionReference;
import io.totokaka.strikk.annotations.StrikkPermission;
import io.totokaka.strikk.annotations.StrikkPermissions;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StrikkPermissionHolder {

    private final StrikkPermissions parent;
    private final StrikkPermission permission;
    private final ExecutableElement element;

    public StrikkPermissionHolder(StrikkPermissions parent, StrikkPermission permission, ExecutableElement element) {
        this.parent = parent;
        this.permission = permission;
        this.element = element;
    }

    public void dump(Map<String, Object> target) {
        Map<String, Object> permissionMap = new HashMap<>();

        if (permission.description().length() > 0) {
            permissionMap.put("description", permission.description());
        }

        permissionMap.put("default", permission.defaultAccess().getValue());

        for (ChildPermissionReference permissionReference : permission.children()) {
            Map<String, Object> childrenMap;
            if (permissionMap.containsKey("children")) {
                childrenMap = (Map<String, Object>) permissionMap.get("children");
            } else {
                childrenMap = new HashMap<>();
                permissionMap.put("children", childrenMap);
            }

            new ChildPermissionReferenceHolder(permissionReference).dump(childrenMap);
        }

        String name = "";
        if (parent.base().length() > 0) {
            name = parent.base() + ".";
        }
        if (permission.name().length() > 0) {
            name += permission.name();
        } else {
            name += element.getSimpleName().toString();
        }
        target.put(name, permissionMap);
    }

    public List<String> children() {
        return Arrays.stream(permission.children())
                .map(ChildPermissionReference::name)
                .collect(Collectors.toList());
    }
}
