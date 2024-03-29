package dev.nincodedo.nincord.command;

import dev.nincodedo.nincord.command.message.MessageContextCommand;
import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.command.user.UserContextCommand;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.release.ReleaseFilter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import javax.annotation.Nonnull;
import java.util.List;

@Slf4j
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
            log.info("Registering commands on {} guild(s)", guilds.size());
            guilds.forEach(this::registerCommands);
            log.info("Finished registering commands");
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        log.info("Registering commands on joined guild {}", FormatLogObject.guildName(event.getGuild()));
        registerCommands(event.getGuild());
        log.trace("Finished registering commands on joined guild {}", FormatLogObject.guildName(event.getGuild()));
    }

    private void registerCommands(Guild guild) {
        if (guild == null) {
            log.trace("Null guild found?");
            return;
        }
        try {
            log.trace("Registering commands for server {}", FormatLogObject.guildName(guild));
            List<CommandData> commandDataList = commands.stream()
                    .filter(Command::isAbleToRegisterOnGuild)
                    .map(this::convertToCommandData)
                    .toList();
            guild.updateCommands()
                    .addCommands(commandDataList)
                    .queue(commandList -> log.info("Successfully registered {} commands on server {}",
                            commandList.size(), FormatLogObject.guildName(guild)));
        } catch (Exception e) {
            log.error("Failed to register commands on guild {}", FormatLogObject.guildName(guild), e);
        }
    }

    /**
     * Convert from Ninbot {@link Command} to JDA {@link CommandData}.
     *
     * @param command Ninbot command to convert
     * @return JDA CommandData
     */
    private CommandData convertToCommandData(Command<?> command) {
        return switch (command) {
            case SlashCommand slashCommand -> {
                SlashCommandData slashCommandData = Commands.slash(slashCommand.getName(),
                                slashCommand.getDescription())
                        .setDefaultPermissions(getDefaultPermissions(slashCommand));
                try {
                    slashCommand.getCommandOptions().forEach(slashCommandData::addOptions);
                    slashCommand.getSubcommandDatas().forEach(slashCommandData::addSubcommands);
                    yield slashCommandData;
                } catch (Exception e) {
                    log.error("Failed to add {}", slashCommand, e);
                }
                yield slashCommandData;
            }
            case UserContextCommand userContextCommand -> Commands.user(userContextCommand.getName())
                    .setDefaultPermissions(getDefaultPermissions(userContextCommand));
            case MessageContextCommand messageContextCommand -> Commands.message(messageContextCommand.getName())
                    .setDefaultPermissions(getDefaultPermissions(messageContextCommand));
            default -> Commands.context(net.dv8tion.jda.api.interactions.commands.Command.Type.UNKNOWN, "null");
        };
    }

    private DefaultMemberPermissions getDefaultPermissions(Command<?> command) {
        return command.isCommandEnabledByDefault() ? DefaultMemberPermissions.ENABLED :
                DefaultMemberPermissions.DISABLED;
    }
}
