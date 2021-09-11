package dev.nincodedo.ninbot.common.command;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
            shardManager.getGuilds().forEach(guild -> {
                        if (guild != null) {
                            List<CommandData> commandDataList = slashCommands.stream()
                                    .map(this::convertToCommandData)
                                    .collect(Collectors.toList());
                            guild.updateCommands().addCommands(commandDataList).queue();
                        }
                    }
            );
        }
    }

    private CommandData convertToCommandData(SlashCommand slashCommand) {
        CommandData commandData = new CommandData(slashCommand
                .getName(), slashCommand.getDescription());
        try {
            if (slashCommand.getName().equals("hugedab")) {
                return commandData;
            }
            slashCommand.getCommandOptions().forEach(commandData::addOptions);
            slashCommand.getSubcommandDatas().forEach(commandData::addSubcommands);
            return commandData;
        } catch (Exception e) {
            log.error("Failed to add {}", slashCommand, e);
        }
        return commandData;
    }
}