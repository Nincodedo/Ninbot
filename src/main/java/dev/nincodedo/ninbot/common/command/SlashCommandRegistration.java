package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.DegreesOfNinbot;
import dev.nincodedo.ninbot.common.release.ReleaseType;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class SlashCommandRegistration extends ListenerAdapter {

    private List<SlashCommand> slashCommands;

    public SlashCommandRegistration(List<SlashCommand> slashCommands) {
        this.slashCommands = slashCommands;
    }

    @Override
    public void onReady(ReadyEvent readyEvent) {
        var shardManager = readyEvent.getJDA().getShardManager();
        if (shardManager != null) {
            var guilds = shardManager.getGuilds();
            log.trace("Registering slash commands on {} guild(s)", guilds.size());
            guilds.forEach(this::registerCommands);
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        log.trace("Registering slash commands on joined guild {}", event.getGuild().getId());
        registerCommands(event.getGuild());
    }

    private void registerCommands(Guild guild) {
        if (guild != null) {
            try {
                log.trace("Registering slash commands for guild {}", guild.getId());
                guild.updateCommands().complete();
                List<CommandData> commandDataList = slashCommands.stream()
                        .filter(slashCommand -> DegreesOfNinbot.releaseAllowed(slashCommand.getReleaseType(), guild))
                        .map(slashCommand -> convertToCommandData(slashCommand, guild.getLocale()))
                        .toList();
                guild.updateCommands().addCommands(commandDataList).queue();
            } catch (Exception e) {
                log.error("Failed to register commands on server {}", guild.getId(), e);
            }
        } else {
            log.trace("Null guild found?");
        }
    }

    private CommandData convertToCommandData(SlashCommand slashCommand, Locale locale) {
        CommandData commandData = new CommandData(slashCommand
                .getName(), slashCommand.getDescription());
        try {
            slashCommand.getCommandOptions().forEach(commandData::addOptions);
            slashCommand.getSubcommandDatas().forEach(commandData::addSubcommands);
            return commandData;
        } catch (Exception e) {
            log.error("Failed to add {}", slashCommand, e);
        }
        return commandData;
    }
}
