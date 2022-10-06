package dev.nincodedo.ninbot.common.message;

import dev.nincodedo.ninbot.common.Emojis;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.List;

public abstract class EphemeralMessageExecutor<T extends MessageExecutor<T>> extends MessageExecutor<T> {
    List<MessageCreateData> ephemeralMessageResponses;
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

    protected abstract ReplyCallbackAction replyMessage(MessageCreateData message);

    protected ReplyCallbackAction replyEphemeralMessage(MessageCreateData message) {
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
        ephemeralMessageResponses.add(new MessageCreateBuilder().addContent(message).build());
        return returnThis();
    }

    public T addEphemeralMessage(MessageCreateData message) {
        ephemeralMessageResponses.add(message);
        return returnThis();
    }

    public T addEphemeralMessage(MessageEmbed messageEmbed) {
        ephemeralMessageEmbeds.add(messageEmbed);
        return returnThis();
    }

    public T addEphemeralUnsuccessfulReaction() {
        return addEphemeralMessage(Emojis.CROSS_X);
    }
}
