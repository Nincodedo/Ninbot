package com.nincraft.ninbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class AbstractCommand {

    int commandLength;

    public abstract void execute(MessageReceivedEvent event);

    boolean isCommandLengthCorrect(String content) {
        return content.split(" ").length == commandLength;
    }
}
