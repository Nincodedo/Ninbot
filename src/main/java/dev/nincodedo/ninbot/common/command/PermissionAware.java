package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.components.config.ConfigService;

public abstract class PermissionAware implements SlashCommand {

    protected ConfigService configService;

    protected PermissionAware(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public boolean shouldCheckPermissions() {
        return true;
    }

    @Override
    public ConfigService configService() {
        return configService;
    }
}
