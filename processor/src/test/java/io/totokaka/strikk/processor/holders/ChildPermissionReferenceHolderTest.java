package io.totokaka.strikk.processor.holders;

import io.totokaka.strikk.annotations.*;
import org.bukkit.permissions.Permission;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ChildPermissionReferenceHolderTest {

    private final static String BASE = "a.permssion.base";
    private final static String POSITIVE_CHILD = ".positive";
    private final static String NEGATIVE_CHILD = ".negative";
    private final static String ABSOLUTE_CHILD = "absolute.children.are.possible";
    private final static String RELATIVE_CHILD = ".relative.children.may.be.long";

    @Test
    public void dumpPositive() throws Exception {
        StrikkPermissions parent = new StrikkPermissionsImplementation(BASE, "");
        ChildPermissionReference child = new ChildPermissionReferenceImplementation(POSITIVE_CHILD, true);

        ChildPermissionReferenceHolder holder = new ChildPermissionReferenceHolder(parent, child);

        Map<String, Object> children = new HashMap<>();
        holder.dump(children);

        assertEquals(children.get(holder.resolveName()), true);
    }

    @Test
    public void dumpNegative() throws Exception {
        StrikkPermissions parent = new StrikkPermissionsImplementation(BASE, "");
        ChildPermissionReference child = new ChildPermissionReferenceImplementation(NEGATIVE_CHILD, false);

        ChildPermissionReferenceHolder holder = new ChildPermissionReferenceHolder(parent, child);

        Map<String, Object> children = new HashMap<>();
        holder.dump(children);

        assertEquals(children.get(holder.resolveName()), false);
    }

    @Test
    public void resolveAbsoluteName() throws Exception {
        StrikkPermissions parent = new StrikkPermissionsImplementation(BASE, "");
        ChildPermissionReference child = new ChildPermissionReferenceImplementation(ABSOLUTE_CHILD, true);

        ChildPermissionReferenceHolder holder = new ChildPermissionReferenceHolder(parent, child);

        assertEquals(holder.resolveName(), ABSOLUTE_CHILD);
    }

    @Test
    public void resolveRelativeName() throws Exception {
        StrikkPermissions parent = new StrikkPermissionsImplementation(BASE, "");
        ChildPermissionReference child = new ChildPermissionReferenceImplementation(RELATIVE_CHILD, true);

        ChildPermissionReferenceHolder holder = new ChildPermissionReferenceHolder(parent, child);

        assertEquals(holder.resolveName(), BASE + RELATIVE_CHILD);
    }

}