package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.config.db.ConfigService;

public abstract class PermissionAware {

    protected ConfigService configService;

    protected PermissionAware(ConfigService configService) {
        this.configService = configService;
    }

    public boolean shouldCheckPermissions() {
        return true;
    }

    public ConfigService configService() {
        return configService;
    }
}
