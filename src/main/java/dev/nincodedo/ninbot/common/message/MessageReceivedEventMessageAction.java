package dev.nincodedo.ninbot.common.message;

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
import java.util.List;

@Getter
public class MessageReceivedEventMessageAction extends MessageAction<MessageReceivedEventMessageAction> {
    private MessageReceivedEvent event;
    private List<Message> privateMessageList;
    private List<Message> channelMessageList;

    public MessageReceivedEventMessageAction() {
        super();
        privateMessageList = new ArrayList<>();
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
            for (String emoji : reactions) {
                eventMessage.addReaction(emoji).queue();
            }
            for (Emote emote : reactionEmotes) {
                eventMessage.addReaction(emote).queue();
            }
        }
    }

    @Override
    public MessageReceivedEventMessageAction returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return event.getChannel();
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

    public MessageReceivedEventMessageAction addChannelAction(String message) {
        channelMessageList.add(new MessageBuilder().append(message).build());
        return this;
    }
}
