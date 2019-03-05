package com.nincraft.ninbot.components.command;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nincraft.ninbot.components.common.Emojis;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandResult
{
    private MessageReceivedEvent event;
    private List<Message>        privateMessageList;
    private List<Message>        channelMessageList;
    private List<Emojis>         emojisList;

    public CommandResult(MessageReceivedEvent event)
    {
        this.event = event;
        privateMessageList = new ArrayList<>();
        emojisList = new ArrayList<>();
        channelMessageList = new ArrayList<>();
    }

    public CommandResult addAction(CommandAction action, Message message)
    {
        switch (action)
        {
            case PRIVATE_MESSAGE:
                privateMessageList.add(message);
                break;
            case CHANNEL_MESSAGE:
                channelMessageList.add(message);
                break;
            default:
                break;
        }
        return this;
    }

    public CommandResult addAction(Emojis... emoji)
    {
        emojisList.addAll(Arrays.asList(emoji));
        return this;
    }

    public void executeActions()
    {

    }
}
