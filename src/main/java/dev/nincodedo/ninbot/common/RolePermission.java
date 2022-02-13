package dev.nincodedo.ninbot.common;

import lombok.Getter;

public enum RolePermission {
    ADMIN(2, "admin"), MODS(1, "mods"), EVERYONE(0, "@everyone");

    @Getter
    final int roleLevel;
    @Getter
    final String roleName;

    RolePermission(int roleLevel, String roleName) {
        this.roleLevel = roleLevel;
        this.roleName = roleName;
    }
}
