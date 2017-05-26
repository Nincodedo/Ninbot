package com.nincraft.ninbot.command;

import com.nincraft.ninbot.Ninbot;
import com.nincraft.ninbot.dao.IEventDao;
import com.nincraft.ninbot.util.MessageSenderHelper;
import lombok.val;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EventCommand extends AbstractCommand {

    private IEventDao eventDao;

    public EventCommand() {
        commandLength = 3;
        commandName = "events";
        commandDescription = "list/add/edit events, use @Ninbot event help for more details";
        eventDao = Ninbot.getEventDao();
    }

    @Override
    public void execute(MessageReceivedEvent messageReceivedEvent) {
        val content = messageReceivedEvent.getMessage().getContent().toLowerCase();
        val channel = messageReceivedEvent.getChannel();
        if (isCommandLengthCorrect(content)) {
            val action = content.split(" ")[2].toLowerCase();
            switch (action) {
                case "list":
                    listEvents(channel);
                    break;
            }
        }
    }

    private void listEvents(MessageChannel channel) {
        MessageSenderHelper.sendMessage(channel, eventDao.getAllEvents().toString());
    }

    @Override
    boolean isCommandLengthCorrect(String content) {
        return content.split(" ").length >= commandLength;
    }
}
