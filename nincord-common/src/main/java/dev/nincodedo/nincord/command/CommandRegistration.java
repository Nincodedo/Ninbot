package dev.nincodedo.nincord.command;

import dev.nincodedo.nincord.command.message.MessageContextCommand;
import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.command.user.UserContextCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CommandRegistration extends ListenerAdapter {

    private final List<Command<?>> commands;

    @Override
    public void onReady(ReadyEvent readyEvent) {
        var shardManager = readyEvent.getJDA().getShardManager();
        if (shardManager == null) {
            log.trace("Null shard manager");
            return;
        }
        var shards = shardManager.getShards();
        log.info("Registering commands on {} shards(s)", shards.size());
        shards.forEach(this::registerCommands);
        log.info("Finished registering commands");
    }

    private void registerCommands(JDA jda) {
        if (jda == null) {
            log.trace("Null shard");
            return;
        }
        try {
            List<CommandData> commandDataList = commands.stream()
                    .filter(command -> !command.allowedIntegrations().contains(IntegrationType.UNKNOWN))
                    .map(this::convertToCommandData)
                    .toList();
            jda.updateCommands()
                    .addCommands(commandDataList)
                    .queue(commandList -> log.info("Successfully registered {} commands on shard {}",
                            commandList.size(), jda.getShardInfo()));
        } catch (Exception e) {
            log.error("Failed to register commands on shard {}", jda.getShardInfo(), e);
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
                        .setDefaultPermissions(slashCommand.getPermissions())
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(slashCommand.allowedIntegrations());
                try {
                    if (!slashCommand.getCommandOptions().isEmpty()) {
                        slashCommand.getCommandOptions().forEach(slashCommandData::addOptions);
                    }
                    if (!slashCommand.getSubcommandDatas().isEmpty()) {
                        slashCommand.getSubcommandDatas().forEach(slashCommandData::addSubcommands);
                    }
                    yield slashCommandData;
                } catch (Exception e) {
                    log.error("Failed to add {}", slashCommand, e);
                }
                yield slashCommandData;
            }
            case UserContextCommand userContextCommand -> Commands.user(userContextCommand.getName())
                    .setDefaultPermissions(userContextCommand.getPermissions())
                    .setContexts(InteractionContextType.ALL)
                    .setIntegrationTypes(userContextCommand.allowedIntegrations());
            case MessageContextCommand messageContextCommand -> Commands.message(messageContextCommand.getName())
                    .setDefaultPermissions(messageContextCommand.getPermissions())
                    .setContexts(InteractionContextType.ALL)
                    .setIntegrationTypes(messageContextCommand.allowedIntegrations());
            default -> Commands.context(net.dv8tion.jda.api.interactions.commands.Command.Type.UNKNOWN, "null");
        };
    }
}
