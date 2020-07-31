package com.nincraft.ninbot.components.common;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class MessageAction {
    private MessageReceivedEvent event;
    private List<Message> privateMessageList;
    private List<Message> channelMessageList;
    private List<String> emojisList;
    private List<Emote> emoteList;
    @Setter
    private Message overrideMessage;

    public MessageAction() {
        privateMessageList = new ArrayList<>();
        emojisList = new ArrayList<>();
        emoteList = new ArrayList<>();
        channelMessageList = new ArrayList<>();
    }

    public MessageAction(MessageReceivedEvent event) {
        this();
        this.event = event;
    }

    public static void successfulReaction(Message message) {
        message.addReaction(Emojis.CHECK_MARK).queue();
    }

    public static void unsuccessfulReaction(Message message) {
        message.addReaction(Emojis.CROSS_X).queue();
    }

    public void executeActions() {
        Message eventMessage = event.getMessage();
        if (overrideMessage != null) {
            eventMessage = overrideMessage;
        }
        for (Message message : privateMessageList) {
            event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendTyping().queue();
                privateChannel.sendMessage(message).queue();
            });
        }
        for (Message message : channelMessageList) {
            if (message != null) {
                MessageChannel messageChannel = event.getChannel();
                messageChannel.sendTyping().queue();
                messageChannel.sendMessage(message).queue();
            }
        }
        for (String emoji : emojisList) {
            eventMessage.addReaction(emoji).queue();
        }
        for (Emote emote : emoteList) {
            eventMessage.addReaction(emote).queue();
        }
    }

    public MessageAction addChannelAction(Message message) {
        channelMessageList.add(message);
        return this;
    }

    public MessageAction addChannelAction(EmbedBuilder embedBuilder) {
        channelMessageList.add(new MessageBuilder(embedBuilder).build());
        return this;
    }

    public MessageAction addPrivateMessageAction(MessageEmbed messageEmbed) {
        return addPrivateMessageAction(new MessageBuilder(messageEmbed).build());
    }

    private MessageAction addPrivateMessageAction(Message message) {
        privateMessageList.add(message);
        return this;
    }

    public MessageAction addReaction(List<String> emoji) {
        emojisList.addAll(emoji);
        return this;
    }

    public MessageAction addReaction(String... emoji) {
        emojisList.addAll(Arrays.asList(emoji));
        return this;
    }

    public MessageAction addUnsuccessfulReaction() {
        addReaction(Emojis.CROSS_X);
        return this;
    }

    public MessageAction addUnknownReaction() {
        addReaction(Emojis.QUESTION_MARK);
        return this;
    }

    public MessageAction addSuccessfulReaction() {
        addReaction(Emojis.CHECK_MARK);
        return this;
    }

    public MessageAction addCorrectReaction(boolean isSuccessful) {
        if (isSuccessful) {
            addSuccessfulReaction();
        } else {
            addUnsuccessfulReaction();
        }
        return this;
    }

    public MessageAction addReactionEmotes(List<Emote> emotes) {
        emoteList.addAll(emotes);
        return this;
    }

    public MessageAction addChannelAction(String message) {
        channelMessageList.add(new MessageBuilder().append(message).build());
        return this;
    }
}
