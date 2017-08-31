package io.totokaka.strikk.processor;

import io.totokaka.strikk.annotations.ChildPermissionReference;

import java.util.Map;

public class ChildPermissionReferenceHolder {

    private final ChildPermissionReference permissionReference;

    public ChildPermissionReferenceHolder(ChildPermissionReference permissionReference) {
        this.permissionReference = permissionReference;
    }

    public void dump(Map<String, Object> target) {
        target.put(permissionReference.name(), permissionReference.positive());
    }
}
