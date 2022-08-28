package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.config.db.Config;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StreamCommand implements SlashCommand {

    private ConfigService configService;

    public StreamCommand(ConfigService configService) {
        this.configService = configService;
    }

    private boolean toggleConfig(String userId, String serverId) {
        var configName = ConfigConstants.STREAMING_ANNOUNCE_USERS;
        var streamingAnnounceUsers = configService.getConfigByName(serverId, configName);
        boolean foundUser = false;
        for (Config config : streamingAnnounceUsers) {
            if (config.getValue().equals(userId)) {
                configService.removeConfig(config);
                foundUser = true;
                break;
            }
        }
        if (!foundUser) {
            configService.addConfig(serverId, configName, userId);
        }
        return foundUser;
    }

    @Override
    public String getName() {
        return StreamCommandName.STREAM.get();
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        boolean isToggled = toggleConfig(slashCommandEvent.getUser().getId(), slashCommandEvent.getGuild().getId());
        String response = "Stream announcements have been turned ";
        messageExecutor.addEphemeralMessage(isToggled ? response + "off." : response + "on.");
        return messageExecutor;
    }
}
