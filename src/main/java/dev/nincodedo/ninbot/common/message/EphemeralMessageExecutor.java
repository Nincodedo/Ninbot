package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;
import java.util.List;

public abstract class EphemeralMessageExecutor<T extends MessageExecutor<T>> extends MessageExecutor<T> {
    List<Message> ephemeralMessageResponses;
    List<MessageEmbed> messageEmbeds;
    List<MessageEmbed> ephemeralMessageEmbeds;

    protected EphemeralMessageExecutor() {
        this.ephemeralMessageResponses = new ArrayList<>();
        this.messageEmbeds = new ArrayList<>();
        this.ephemeralMessageEmbeds = new ArrayList<>();
    }

    @Override
    public void executeMessageActions() {
        if (!messageResponses.isEmpty()) {
            messageResponses.forEach(message -> replyMessage(message).queue());
        }
        if (!ephemeralMessageResponses.isEmpty()) {
            ephemeralMessageResponses.forEach(message -> replyEphemeralMessage(message).queue());
        }
        if (!messageEmbeds.isEmpty()) {
            replyEmbeds(messageEmbeds).queue();
        }
        if (!ephemeralMessageEmbeds.isEmpty()) {
            replyEphemeralEmbeds(ephemeralMessageEmbeds).queue();
        }
    }

    protected abstract ReplyCallbackAction replyMessage(Message message);

    protected ReplyCallbackAction replyEphemeralMessage(Message message) {
        return replyMessage(message).setEphemeral(true);
    }

    protected abstract ReplyCallbackAction replyEmbeds(List<MessageEmbed> messageEmbeds);

    protected ReplyCallbackAction replyEphemeralEmbeds(List<MessageEmbed> messageEmbeds) {
        return replyEmbeds(messageEmbeds).setEphemeral(true);
    }

    public T addMessageEmbed(MessageEmbed messageEmbed) {
        messageEmbeds.add(messageEmbed);
        return returnThis();
    }

    public T addEphemeralMessage(String message) {
        ephemeralMessageResponses.add(new MessageBuilder().append(message).build());
        return returnThis();
    }

    public T addEphemeralMessage(Message message) {
        ephemeralMessageResponses.add(message);
        return returnThis();
    }

    public T addEphemeralMessage(MessageEmbed messageEmbed) {
        ephemeralMessageEmbeds.add(messageEmbed);
        return returnThis();
    }
}
