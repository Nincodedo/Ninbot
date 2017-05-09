package com.nincraft.ninbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ICommand {
    void execute(MessageReceivedEvent event);
}
