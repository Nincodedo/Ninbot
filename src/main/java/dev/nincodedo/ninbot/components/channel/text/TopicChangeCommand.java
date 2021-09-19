package dev.nincodedo.ninbot.components.channel.text;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.RolePermission;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.components.config.Config;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public RolePermission getRolePermission() {
        return RolePermission.MODS;
    }

    @Override
    public void execute(SlashCommandEvent event) {
        var channelConfig = configService.getConfigByServerIdAndName(event.getGuild()
                .getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL);
        channelConfig.ifPresentOrElse(config -> {
            configService.removeConfig(config);
            event.reply(Emojis.OFF).setEphemeral(true).queue();
        }, () -> {
            Config config = new Config(event.getGuild()
                    .getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL, event.getChannel().getId());
            configService.addConfig(config);
            event.reply(Emojis.ON).setEphemeral(true).queue();
        });
    }
}
