package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.components.config.ConfigService;

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
