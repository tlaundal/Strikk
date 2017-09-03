package io.totokaka.strikk.processor.holders;

import io.totokaka.strikk.annotations.ChildPermissionReference;
import io.totokaka.strikk.annotations.StrikkPermission;
import io.totokaka.strikk.annotations.StrikkPermissions;

import java.util.Map;

public class ChildPermissionReferenceHolder {

    private final StrikkPermissions parent;
    private final ChildPermissionReference permissionReference;

    public ChildPermissionReferenceHolder(StrikkPermissions parent, ChildPermissionReference permissionReference) {
        this.parent = parent;
        this.permissionReference = permissionReference;
    }

    public void dump(Map<String, Object> target) {
        target.put(resolveName(), permissionReference.positive());
    }

    String resolveName() {
        if (permissionReference.name().startsWith(".")) {
            return parent.base() + permissionReference.name();
        } else {
            return permissionReference.name();
        }
    }
}
