package io.totokaka.strikk.processor.permission;

import io.totokaka.strikk.annotations.ChildPermissionReference;
import io.totokaka.strikk.annotations.PermissionDefault;
import io.totokaka.strikk.annotations.StrikkPermission;

import javax.lang.model.element.Element;

public class StrikkPermissionInterpreter {

    private final StrikkPermissionsInterpreter parent;
    private final Element element;
    private final StrikkPermission annotation;

    public StrikkPermissionInterpreter(StrikkPermissionsInterpreter parent, Element element, StrikkPermission annotation) {
        this.parent = parent;
        this.element = element;
        this.annotation = annotation;
    }

    public String getName() {
        String name = parent.getBase();
        if (annotation.name().length() > 0) {
            name += annotation.name();
        } else {
            name += "." + element.getSimpleName().toString();
        }
        name = name.replaceAll("^\\.+", "").replaceAll("\\.+", ".");
        return name;
    }

    public String getDescription() {
        return annotation.description();
    }

    public PermissionDefault getDefaultAccess() {
        return annotation.defaultAccess();
    }

    public ChildPermissionReference[] resolveChildren() {
        ChildPermissionReference[] children = new ChildPermissionReference[annotation.children().length];
        for (int i = 0; i < children.length; i++) {
            ChildPermissionReference original = annotation.children()[i];
            ChildPermissionReferenceInterpreter interpreter = new ChildPermissionReferenceInterpreter(parent, original);

            children[i] = interpreter.getFullyResolvedCopy();
        }
        return children;
    }

}
