package dev.nincodedo.ninbot.common.message;

import dev.nincodedo.ninbot.common.Emojis;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class EphemeralMessageExecutor extends MessageExecutor {
    List<MessageCreateData> ephemeralMessageResponses;
    List<MessageEmbed> messageEmbeds;
    List<MessageEmbed> ephemeralMessageEmbeds;
    Modal modal;

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
        if (modal != null) {
            replyModal(modal).queue();
        }
    }

    protected abstract ModalCallbackAction replyModal(Modal modal);

    public void addModal(Modal modal) {
        this.modal = modal;
    }

    /**
     * Returns the {@link ReplyCallbackAction} right away instead of queuing everything up for the end. Good if you need
     * access to the {@link ReplyCallbackAction#queue()} result.
     *
     * @param message the {@link Message} being sent
     * @return the {@link ReplyCallbackAction}
     */
    protected abstract ReplyCallbackAction replyMessage(MessageCreateData message);

    protected ReplyCallbackAction replyEphemeralMessage(MessageCreateData message) {
        return replyMessage(message).setEphemeral(true);
    }

    protected abstract ReplyCallbackAction replyEmbeds(List<MessageEmbed> messageEmbeds);

    protected ReplyCallbackAction replyEphemeralEmbeds(List<MessageEmbed> messageEmbeds) {
        return replyEmbeds(messageEmbeds).setEphemeral(true);
    }

    public void addMessageEmbed(MessageEmbed messageEmbed) {
        messageEmbeds.add(messageEmbed);
    }

    public void addEphemeralMessage(String message) {
        ephemeralMessageResponses.add(new MessageCreateBuilder().addContent(message).build());
    }

    public void addEphemeralMessage(MessageCreateData message) {
        ephemeralMessageResponses.add(message);
    }

    public void addEphemeralUnsuccessfulReaction() {
        addEphemeralMessage(Emojis.CROSS_X);
    }
}
