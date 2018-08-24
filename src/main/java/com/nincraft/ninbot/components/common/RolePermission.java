package com.nincraft.ninbot.components.common;

import lombok.Getter;

public enum RolePermission {
    OWNER(3, "86958766125244416"), ADMIN(2, "admin"), MODS(1, "mods"), EVERYONE(0, "@everyone");

    @Getter
    int roleLevel;
    @Getter
    String roleName;

    RolePermission(int roleLevel, String roleName) {
        this.roleLevel = roleLevel;
        this.roleName = roleName;
    }
}
