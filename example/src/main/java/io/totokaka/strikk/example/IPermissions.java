package io.totokaka.strikk.example;

import io.totokaka.strikk.annotations.ChildPermissionReference;
import io.totokaka.strikk.annotations.PermissionDefault;
import io.totokaka.strikk.annotations.StrikkPermission;
import io.totokaka.strikk.annotations.StrikkPermissions;
import org.bukkit.permissions.Permission;

@StrikkPermissions(
        base = IPermissions.BASE
)
public interface IPermissions {

    String BASE = "strikkexample.commands";
    String ALL = ".*";
    String ACCESS = ".access";

    @StrikkPermission(
            name = ALL,
            description = "Gives access to all commands",
            defaultAccess = PermissionDefault.FALSE,
            children = {
                    @ChildPermissionReference(name = ACCESS)
            }
    )
    Permission all();

    @StrikkPermission(
            description = "Gives access to the /access command"
    )
    Permission access();

}
