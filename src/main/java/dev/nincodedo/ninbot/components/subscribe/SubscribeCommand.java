package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.components.command.SlashCommand;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.fun.pathogen.PathogenConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SubscribeCommand implements SlashCommand {

    private ConfigService configService;

    public SubscribeCommand(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        var server = slashCommandEvent.getGuild();
        var role = slashCommandEvent.getOption("subscription").getAsRole();
        if (isValidSubscribeRole(role, slashCommandEvent.getGuild().getId())) {
            addOrRemoveSubscription(slashCommandEvent, server, role);
            slashCommandEvent.reply(Emojis.CHECK_MARK).setEphemeral(true).queue();
        } else {
            slashCommandEvent.reply(resourceBundle().getString("")).setEphemeral(true).queue();
        }
    }

    void addOrRemoveSubscription(SlashCommandEvent event, Guild guild, Role role) {
        guild.addRoleToMember(event.getMember(), role).queue();
    }

    private boolean isValidSubscribeRole(Role role, String serverId) {
        List<String> roleDenyList = configService.getValuesByName(serverId, ConfigConstants.ROLE_DENY_LIST);
        roleDenyList.add(PathogenConfig.getINFECTED_ROLE_NAME());
        return role != null && !roleDenyList.contains(role.getName());
    }

    @Override
    public String getName() {
        return "subscribe";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.ROLE, "subscription", "Role you want subscribe to.", true));
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }
}
