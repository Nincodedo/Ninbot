package dev.nincodedo.ninbot.common.message;

import dev.nincodedo.ninbot.common.Emojis;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class MessageReceivedEventMessageAction implements MessageAction {
    private MessageReceivedEvent event;
    private List<Message> privateMessageList;
    private List<Message> channelMessageList;
    private List<String> emojisList;
    private List<Emote> emoteList;
    private Message overrideMessage;

    public MessageReceivedEventMessageAction() {
        privateMessageList = new ArrayList<>();
        emojisList = new ArrayList<>();
        emoteList = new ArrayList<>();
        channelMessageList = new ArrayList<>();
    }

    public MessageReceivedEventMessageAction(MessageReceivedEvent event) {
        this();
        this.event = event;
    }

    public MessageReceivedEventMessageAction(PrivateMessageReceivedEvent event) {
        this();
        this.event = new MessageReceivedEvent(event.getJDA(), event.getResponseNumber(), event.getMessage());
    }

    public static void successfulReaction(Message message) {
        message.addReaction(Emojis.CHECK_MARK).queue();
    }

    public static void unsuccessfulReaction(Message message) {
        message.addReaction(Emojis.CROSS_X).queue();
    }

    public MessageReceivedEventMessageAction setOverrideMessage(Message overrideMessage) {
        this.overrideMessage = overrideMessage;
        return this;
    }

    @Override
    public void executeActions() {
        Message eventMessage = null;
        if (event != null) {
            eventMessage = event.getMessage();
        }
        if (overrideMessage != null) {
            eventMessage = overrideMessage;
        }
        if (event != null) {
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
        }
        if (eventMessage != null) {
            for (String emoji : emojisList) {
                eventMessage.addReaction(emoji).queue();
            }
            for (Emote emote : emoteList) {
                eventMessage.addReaction(emote).queue();
            }
        }
    }

    public MessageReceivedEventMessageAction addChannelAction(Message message) {
        channelMessageList.add(message);
        return this;
    }

    public MessageReceivedEventMessageAction addChannelAction(EmbedBuilder embedBuilder) {
        channelMessageList.add(new MessageBuilder(embedBuilder).build());
        return this;
    }

    public MessageReceivedEventMessageAction addPrivateMessageAction(MessageEmbed messageEmbed) {
        return addPrivateMessageAction(new MessageBuilder(messageEmbed).build());
    }

    private MessageReceivedEventMessageAction addPrivateMessageAction(Message message) {
        privateMessageList.add(message);
        return this;
    }

    public MessageReceivedEventMessageAction addReaction(List<String> emoji) {
        emojisList.addAll(emoji);
        return this;
    }

    public MessageReceivedEventMessageAction addReaction(String... emoji) {
        emojisList.addAll(Arrays.asList(emoji));
        return this;
    }

    public MessageReceivedEventMessageAction addUnsuccessfulReaction() {
        addReaction(Emojis.CROSS_X);
        return this;
    }

    public MessageReceivedEventMessageAction addUnknownReaction() {
        addReaction(Emojis.QUESTION_MARK);
        return this;
    }

    public MessageReceivedEventMessageAction addSuccessfulReaction() {
        addReaction(Emojis.CHECK_MARK);
        return this;
    }

    public MessageReceivedEventMessageAction addCorrectReaction(boolean isSuccessful) {
        if (isSuccessful) {
            addSuccessfulReaction();
        } else {
            addUnsuccessfulReaction();
        }
        return this;
    }

    public MessageReceivedEventMessageAction addReactionEmotes(List<Emote> emotes) {
        emoteList.addAll(emotes);
        return this;
    }

    public MessageReceivedEventMessageAction addChannelAction(String message) {
        channelMessageList.add(new MessageBuilder().append(message).build());
        return this;
    }
}
