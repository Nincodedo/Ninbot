package dev.nincodedo.ninbot.components.common;

import dev.nincodedo.ninbot.components.command.SlashCommand;
import lombok.val;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SlashCommandRegistration extends ListenerAdapter {

    private List<SlashCommand> slashCommands;

    public SlashCommandRegistration(List<SlashCommand> slashCommands) {
        this.slashCommands = slashCommands;
    }

    @Override
    public void onReady(ReadyEvent readyEvent) {
        val shardManager = readyEvent.getJDA().getShardManager();
        val guildIds = Arrays.asList(Constants.OCW_SERVER_ID, "497444318768922633");
        guildIds.forEach(id -> {
            if (shardManager != null) {
                val guild = shardManager.getGuildById(id);
                if (guild != null) {
                    CommandListUpdateAction commandUpdateAction = guild.updateCommands();
                    guild.retrieveCommands()
                            .complete()
                            .forEach(command -> guild.deleteCommandById(command.getId()).complete());
                    slashCommands.forEach(slashCommand -> {
                        if (slashCommand.getName().equals("hugedab")) {
                            return;
                        }
                        CommandData commandData = new CommandData(slashCommand
                                .getName(), slashCommand.getDescription());
                        slashCommand.getCommandOptions()
                                .forEach(commandOption -> commandData.addOptions(new OptionData(commandOption
                                        .type(), commandOption.name(), commandOption.description()).setRequired(commandOption
                                        .required())));
                        commandUpdateAction.addCommands(commandData).queue();
                    });
                }
            }
        });
    }
}
