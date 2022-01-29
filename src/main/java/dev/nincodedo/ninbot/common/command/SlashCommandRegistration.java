package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.DegreesOfNinbot;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
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
                List<SlashCommandData> slashCommandDataList = slashCommands.stream()
                        .filter(slashCommand -> DegreesOfNinbot.releaseAllowed(slashCommand.getReleaseType(), guild))
                        .map(slashCommand -> convertToSlashCommandData(slashCommand, guild.getLocale()))
                        .toList();
                guild.updateCommands().addCommands(slashCommandDataList).queue();
            } catch (Exception e) {
                log.error("Failed to register commands on server {}", guild.getId(), e);
            }
        } else {
            log.trace("Null guild found?");
        }
    }

    private SlashCommandData convertToSlashCommandData(SlashCommand slashCommand, Locale locale) {
        SlashCommandData slashCommandData = Commands.slash(slashCommand.getName(), slashCommand.getDescription());
        try {
            slashCommand.getCommandOptions().forEach(slashCommandData::addOptions);
            slashCommand.getSubcommandDatas().forEach(slashCommandData::addSubcommands);
            return slashCommandData;
        } catch (Exception e) {
            log.error("Failed to add {}", slashCommand, e);
        }
        return slashCommandData;
    }
}
