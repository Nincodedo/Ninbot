package dev.nincodedo.ninbot.components.slashcommands;

import dev.nincodedo.ninbot.components.common.Constants;
import lombok.val;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SlashCommandRegistration extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent readyEvent) {
        val shardManager = readyEvent.getJDA().getShardManager();
        val guildIds = Arrays.asList(Constants.OCW_SERVER_ID, "497444318768922633");
        guildIds.forEach(id -> {
            if (shardManager != null) {
                val guild = shardManager.getGuildById(id);
                if (guild != null) {
                    CommandUpdateAction commandUpdateAction = guild.updateCommands();
                    guild.retrieveCommands().complete().forEach(command -> guild.deleteCommandById(command.getId()).complete());
                    commandUpdateAction.addCommands(
                            new CommandUpdateAction.CommandData("8ball", "Summons the all knowing 8 ball")
                                    .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING,
                                            "question", "Your question to the 8 ball")
                                            .setRequired(false))
                    ).queue();
                }
            }
        });
    }
}
