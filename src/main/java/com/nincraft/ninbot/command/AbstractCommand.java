package com.nincraft.ninbot.command;

import lombok.Data;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@Data
public abstract class AbstractCommand {

    int commandLength;
    String commandDescription;
    String commandName;

    public abstract void execute(MessageReceivedEvent event);

    boolean isCommandLengthCorrect(String content) {
        return content.split(" ").length == commandLength;
    }
}
