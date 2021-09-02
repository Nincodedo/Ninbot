package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.components.command.SlashCommand;
import dev.nincodedo.ninbot.components.config.Config;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StreamCommand implements SlashCommand {

    private ConfigService configService;

    public StreamCommand(ConfigService configService) {
        this.configService = configService;
    }

    private boolean toggleConfig(String userId, String serverId) {
        var configName = ConfigConstants.STREAMING_ANNOUNCE_USERS;
        var streamingAnnounceUsers = configService.getConfigByName(serverId, configName);
        boolean foundUser = true;
        for (Config config : streamingAnnounceUsers) {
            if (config.getValue().equals(userId)) {
                configService.removeConfig(config);
                foundUser = false;
                break;
            }
        }
        if (foundUser) {
            configService.addConfig(serverId, configName, userId);
        }
        return foundUser;
    }

    @Override
    public String getName() {
        return "stream";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        boolean isToggled = toggleConfig(slashCommandEvent.getUser().getId(), slashCommandEvent.getGuild().getId());
        String response = "Stream announcements have been turned ";
        slashCommandEvent.reply(isToggled ? response + "on." : response + "off.").setEphemeral(true).queue();
    }
}
