package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.command.component.Button;
import dev.nincodedo.ninbot.common.command.component.ButtonInteractionCommand;
import dev.nincodedo.ninbot.common.config.db.Config;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StreamButtonInteractionCommand implements ButtonInteractionCommand {

    private ConfigService configService;

    public StreamButtonInteractionCommand(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public MessageExecutor<ButtonInteractionCommandMessageExecutor> executeButtonPress(
            @NotNull ButtonInteractionEvent event, @NotNull Button button) {
        var messageExecutor = new ButtonInteractionCommandMessageExecutor(event);
        var buttonAction = StreamCommandName.Button.valueOf(button.action().toUpperCase());
        if (buttonAction == StreamCommandName.Button.NOTHING) {
            messageExecutor.editEphemeralMessage(resource("button.stream.nothing"))
                    .clearComponents();
        } else if (buttonAction == StreamCommandName.Button.TOGGLE) {
            var found = toggleConfig(event.getUser().getId(), event.getGuild().getId());
            var onOff = found ? "off" : "on";
            messageExecutor.editEphemeralMessage(resource("button.stream.toggle." + onOff))
                    .clearComponents();
        }
        return messageExecutor;
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
}
