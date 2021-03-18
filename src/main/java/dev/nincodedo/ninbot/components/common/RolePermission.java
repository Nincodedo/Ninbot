package dev.nincodedo.ninbot.components.common;

public enum RolePermission {
    ADMIN(2, "admin"), MODS(1, "mods"), EVERYONE(0, "@everyone");
    int roleLevel;
    String roleName;

    RolePermission(int roleLevel, String roleName) {
        this.roleLevel = roleLevel;
        this.roleName = roleName;
    }


    public int getRoleLevel() {
        return this.roleLevel;
    }


    public String getRoleName() {
        return this.roleName;
    }
}
