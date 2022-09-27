package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.config.db.Config;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StreamCommand implements SlashCommand {

    private ConfigService configService;

    public StreamCommand(ConfigService configService) {
        this.configService = configService;
    }

    private boolean isAnnouncementsEnabledForUser(String userId, String serverId) {
        var streamingAnnounceUsers = configService.getConfigByName(serverId, ConfigConstants.STREAMING_ANNOUNCE_USERS);
        for (Config config : streamingAnnounceUsers) {
            if (config.getValue().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return StreamCommandName.STREAM.get();
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> execute(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        var userId = slashCommandEvent.getUser().getId();
        var announcementsEnabled = isAnnouncementsEnabledForUser(userId, slashCommandEvent.getGuild().getId());
        var onOff = announcementsEnabled ? "on" : "off";
        var opposite = announcementsEnabled ? "off" : "on";
        var createBuilder = new MessageCreateBuilder();
        messageExecutor.addEphemeralMessage(createBuilder.addContent(
                        "Stream announcements are currently " + onOff +
                                ". Would you like to turn them " + opposite + "?")
                .addComponents(ActionRow.of(getPrimaryButton(userId, opposite), getSecondaryButton(userId, onOff)))
                .build());
        return messageExecutor;
    }

    @NotNull
    private Button getSecondaryButton(String userId, String onOff) {
        return Button.secondary("stream-nothing-"+userId, "No, keep them " + onOff);
    }

    @NotNull
    private Button getPrimaryButton(String userId, String opposite) {
        return Button.primary("stream-toggle-"+userId, "Yes, turn them " + opposite);
    }
}
