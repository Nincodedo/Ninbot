package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.command.message.MessageContextCommand;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.command.user.UserContextCommand;
import dev.nincodedo.ninbot.common.logging.UtilLogging;
import dev.nincodedo.ninbot.common.release.ReleaseFilter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

@Slf4j
@Component
public class CommandRegistration extends ListenerAdapter {

    private List<Command> commands;
    private ReleaseFilter releaseFilter;

    public CommandRegistration(List<Command> commands, ReleaseFilter releaseFilter) {
        this.commands = commands;
        this.releaseFilter = releaseFilter;
    }

    @Override
    public void onReady(ReadyEvent readyEvent) {
        var shardManager = readyEvent.getJDA().getShardManager();
        if (shardManager != null) {
            var guilds = shardManager.getGuilds();
            log.trace("Registering commands on {} guild(s)", guilds.size());
            guilds.forEach(this::registerCommands);
            log.trace("Finished registering commands");
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        log.trace("Registering commands on joined guild {}", UtilLogging.logGuildName(event.getGuild()));
        registerCommands(event.getGuild());
        log.trace("Finished registering commands on joined guild {}", UtilLogging.logGuildName(event.getGuild()));
    }

    private void registerCommands(Guild guild) {
        if (guild == null) {
            log.trace("Null guild found?");
            return;
        }
        try {
            log.trace("Registering commands for server {}", UtilLogging.logGuildName(guild));
            var currentCommandList = guild.retrieveCommands().complete();
            List<CommandData> commandDataList = commands.stream()
                    .filter(command -> releaseFilter.filter(command.getReleaseType(), guild))
                    .map(this::convertToCommandData)
                    .toList();
            if (guildHasAllCommands(commandDataList, currentCommandList)) {
                log.trace("Server {} already has all the current commands. Skipping update.",
                        UtilLogging.logGuildName(guild));
            } else {
                guild.updateCommands()
                        .addCommands(commandDataList)
                        .queue(commandList -> log.trace("Successfully registered {} commands on server {}",
                                commandList.size(), UtilLogging.logGuildName(guild)));
            }
        } catch (Exception e) {
            log.error("Failed to register commands on guild {}", UtilLogging.logGuildName(guild), e);
        }
    }

    private boolean guildHasAllCommands(List<CommandData> commandDataList,
            List<net.dv8tion.jda.api.interactions.commands.Command> currentCommandList) {
        if (commandDataList.size() != currentCommandList.size()) {
            return false;
        }
        for (var command : currentCommandList) {
            if (commandDataList.stream().anyMatch(commandData -> commandData.getType() == command.getType())
                    && commandDataList.stream()
                    .filter(commandData -> commandData.getType() == command.getType())
                    .noneMatch(commandData -> commandData.getName().equals(command.getName()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert from Ninbot {@link Command} to JDA {@link CommandData}.
     *
     * @param command Ninbot command to convert
     * @return JDA CommandData
     */
    private CommandData convertToCommandData(Command command) {
        switch (command) {
            case SlashCommand slashCommand:
                SlashCommandData slashCommandData = Commands.slash(slashCommand.getName(),
                        slashCommand.getDescription());
                try {
                    slashCommand.getCommandOptions().forEach(slashCommandData::addOptions);
                    slashCommand.getSubcommandDatas().forEach(slashCommandData::addSubcommands);
                    return slashCommandData;
                } catch (Exception e) {
                    log.error("Failed to add {}", slashCommand, e);
                }
                return slashCommandData;
            case UserContextCommand userContextCommand:
                return Commands.user(userContextCommand.getName());
            case MessageContextCommand messageContextCommand:
                return Commands.message(messageContextCommand.getName());
            case null:
            default:
                return Commands.context(net.dv8tion.jda.api.interactions.commands.Command.Type.UNKNOWN, "null");
        }
    }
}