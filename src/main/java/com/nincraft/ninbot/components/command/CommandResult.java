package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.common.Emojis;
import lombok.Getter;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class CommandResult {
    private MessageReceivedEvent event;
    private List<Message> privateMessageList;
    private List<Message> channelMessageList;
    private List<String> emojisList;
    private List<Emote> emoteList;

    public CommandResult(MessageReceivedEvent event) {
        this.event = event;
        privateMessageList = new ArrayList<>();
        emojisList = new ArrayList<>();
        emoteList = new ArrayList<>();
        channelMessageList = new ArrayList<>();
    }

    void executeActions() {
        for (Message message : privateMessageList) {
            PrivateChannel privateChannel = event.getAuthor().openPrivateChannel().complete();
            privateChannel.sendTyping().queue();
            privateChannel.sendMessage(message).queue();
        }
        for (Message message : channelMessageList) {
            MessageChannel messageChannel = event.getChannel();
            messageChannel.sendTyping().queue();
            messageChannel.sendMessage(message).queue();
        }
        for (String emoji : emojisList) {
            event.getMessage().addReaction(emoji).queue();
        }
        for (Emote emote : emoteList) {
            event.getMessage().addReaction(emote).queue();
        }
    }

    public CommandResult addChannelAction(Message message) {
        channelMessageList.add(message);
        return this;
    }

    public CommandResult addPrivateMessageAction(Message message) {
        privateMessageList.add(message);
        return this;
    }

    public CommandResult addReaction(List<String> emoji) {
        emojisList.addAll(emoji);
        return this;
    }

    public CommandResult addReaction(String... emoji) {
        emojisList.addAll(Arrays.asList(emoji));
        return this;
    }

    public CommandResult addUnsuccessfulReaction() {
        addReaction(Emojis.CROSS_X);
        return this;
    }

    public CommandResult addUnknownReaction() {
        addReaction(Emojis.QUESTION_MARK);
        return this;
    }

    public CommandResult addSuccessfulReaction() {
        addReaction(Emojis.CHECK_MARK);
        return this;
    }

    public CommandResult addCorrectReaction(boolean isSuccessful) {
        if (isSuccessful) {
            addSuccessfulReaction();
        } else {
            addUnsuccessfulReaction();
        }
        return this;
    }

    public CommandResult addReactionEmotes(List<Emote> emotes) {
        emoteList.addAll(emotes);
        return this;
    }

    public CommandResult addChannelAction(String message) {
        channelMessageList.add(new MessageBuilder().append(message).build());
        return this;
    }
}
