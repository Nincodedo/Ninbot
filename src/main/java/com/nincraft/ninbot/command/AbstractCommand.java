package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageSenderHelper;
import lombok.Data;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@Data
public abstract class AbstractCommand {

    int commandLength;
    String commandDescription;
    String commandName;
    boolean hidden;

    public abstract void execute(MessageReceivedEvent event);

    boolean isCommandLengthCorrect(String content) {
        return content.split(" ").length == commandLength;
    }

    void wrongCommandLengthMessage(MessageChannel channel) {
        MessageSenderHelper.sendMessage(channel, "Wrong number of arguments for %s command", commandName);
    }

    String getSubcommand(String command) {
        return command.split(" ")[2].toLowerCase();
    }
}
