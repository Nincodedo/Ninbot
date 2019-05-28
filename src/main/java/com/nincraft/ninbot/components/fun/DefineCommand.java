package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
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
        MessageBuilderHelper messageBuilderHelper = new MessageBuilderHelper();
        messageBuilderHelper.setTitle("Definition of " + word);
        messageBuilderHelper.addField(word, definition.get("definition").split("\n")[0], false);
        messageBuilderHelper.addField("Find out more", definition.get("permalink"), false);
        return messageBuilderHelper.build();
    }
}
