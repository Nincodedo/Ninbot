package dev.nincodedo.ninbot.components.channel.text;

import dev.nincodedo.ninbot.common.Emojis;
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
public class TopicChangeCommand implements SlashCommand {

    private ConfigService configService;

    public TopicChangeCommand(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public String getName() {
        return TopicChangeCommandName.TOPICCHANGE.get();
    }

    @Override
    public boolean isCommandEnabledByDefault() {
        return false;
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent event) {
        var messageExecutor = new SlashCommandEventMessageExecutor(event);
        var guild = event.getGuild();
        if (guild == null) {
            return messageExecutor;
        }
        var channelConfig = configService.getConfigByServerIdAndName(guild.getId(),
                ConfigConstants.TOPIC_CHANGE_CHANNEL);
        if (channelConfig.isPresent()) {
            var config = channelConfig.get();
            configService.removeConfig(config);
            messageExecutor.addEphemeralMessage(Emojis.OFF);
        } else {
            Config config = new Config(guild.getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL, event.getChannel().getId());
            configService.addConfig(config);
            messageExecutor.addEphemeralMessage(Emojis.ON);
        }
        return messageExecutor;
    }
}
