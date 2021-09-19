package dev.nincodedo.ninbot.common.message;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MessageReceivedEventMessageExecutor extends MessageExecutor<MessageReceivedEventMessageExecutor> {
    private MessageReceivedEvent event;
    private List<Message> privateMessageList;
    private List<Message> channelMessageList;

    public MessageReceivedEventMessageExecutor() {
        super();
        privateMessageList = new ArrayList<>();
        channelMessageList = new ArrayList<>();
    }

    public MessageReceivedEventMessageExecutor(MessageReceivedEvent event) {
        this();
        this.event = event;
    }

    public MessageReceivedEventMessageExecutor(PrivateMessageReceivedEvent event) {
        this();
        this.event = new MessageReceivedEvent(event.getJDA(), event.getResponseNumber(), event.getMessage());
    }

    @Override
    public void executeActions() {
        super.executeActions();
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
    }

    @Override
    public MessageReceivedEventMessageExecutor returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return event.getChannel();
    }

    @Override
    public Message getMessage() {
        return overrideMessage != null ? overrideMessage : event.getMessage();
    }

    public MessageReceivedEventMessageExecutor addChannelAction(Message message) {
        channelMessageList.add(message);
        return this;
    }

    public MessageReceivedEventMessageExecutor addChannelAction(EmbedBuilder embedBuilder) {
        channelMessageList.add(new MessageBuilder(embedBuilder).build());
        return this;
    }

    public MessageReceivedEventMessageExecutor addPrivateMessageAction(MessageEmbed messageEmbed) {
        return addPrivateMessageAction(new MessageBuilder(messageEmbed).build());
    }

    private MessageReceivedEventMessageExecutor addPrivateMessageAction(Message message) {
        privateMessageList.add(message);
        return this;
    }

    public MessageReceivedEventMessageExecutor addChannelAction(String message) {
        channelMessageList.add(new MessageBuilder().append(message).build());
        return this;
    }
}
