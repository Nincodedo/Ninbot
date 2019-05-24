package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefineCommand extends AbstractCommand {

    private DefineWordAPI defineWordAPI;

    public DefineCommand(DefineWordAPI defineWordAPI) {
        name = "define";
        length = 3;
        checkExactLength = false;
        helpText = "@Ninbot define WORD";
        this.defineWordAPI = defineWordAPI;
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        String word = event.getMessage().getContentStripped().substring(("@Ninbot " + name + " ").length());
        Map<String, String> definition = defineWordAPI.defineWord(word);
        Message message = buildMessage(definition, word);
        commandResult.addChannelAction(message);
        return commandResult;
    }

    private Message buildMessage(Map<String, String> definition, String word) {
        return new MessageBuilder(
                new EmbedBuilder()
                        .setTitle("Definition of " + word)
                        .addField(word, definition.get("definition").split("\n")[0], false)
                        .addField("Find out more", definition.get("permalink"), false)).build();
    }
}
