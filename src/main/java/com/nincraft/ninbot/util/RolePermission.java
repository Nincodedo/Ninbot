package com.nincraft.ninbot.util;

import lombok.Getter;

public enum RolePermission {
    ADMIN(2, "admin"), MODS(1, "mods"), EVERYONE(0, "@everyone");

    @Getter
    int roleLevel;
    @Getter
    String roleName;

    RolePermission(int roleLevel, String roleName) {
        this.roleLevel = roleLevel;
        this.roleName = roleName;
    }
}
