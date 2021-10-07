package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandEventMessageExecutor extends MessageExecutor<SlashCommandEventMessageExecutor> {

    private SlashCommandEvent slashCommandEvent;
    private List<Message> ephemeralMessageResponses;
    private List<MessageEmbed> messageEmbeds;
    private List<MessageEmbed> ephemeralMessageEmbeds;

    public SlashCommandEventMessageExecutor(SlashCommandEvent slashCommandEvent) {
        super();
        this.slashCommandEvent = slashCommandEvent;
        this.ephemeralMessageResponses = new ArrayList<>();
        this.messageEmbeds = new ArrayList<>();
        this.ephemeralMessageEmbeds = new ArrayList<>();
    }

    public void executeMessageActions() {
        if (!messageResponses.isEmpty()) {
            messageResponses.forEach(message -> slashCommandEvent.reply(message).queue());
        }
        if (!ephemeralMessageResponses.isEmpty()) {
            ephemeralMessageResponses.forEach(message -> slashCommandEvent.reply(message).setEphemeral(true).queue());
        }
        if (!messageEmbeds.isEmpty()) {
            slashCommandEvent.replyEmbeds(messageEmbeds).queue();
        }
        if (!ephemeralMessageEmbeds.isEmpty()) {
            slashCommandEvent.replyEmbeds(ephemeralMessageEmbeds).setEphemeral(true).queue();
        }
    }

    public SlashCommandEventMessageExecutor addMessageEmbed(MessageEmbed messageEmbed) {
        messageEmbeds.add(messageEmbed);
        return returnThis();
    }

    public SlashCommandEventMessageExecutor addEphemeralMessage(String message) {
        ephemeralMessageResponses.add(new MessageBuilder().append(message).build());
        return returnThis();
    }

    public SlashCommandEventMessageExecutor addEphemeralMessage(Message message) {
        ephemeralMessageResponses.add(message);
        return returnThis();
    }

    public SlashCommandEventMessageExecutor addEphemeralMessage(MessageEmbed messageEmbed) {
        ephemeralMessageEmbeds.add(messageEmbed);
        return returnThis();
    }

    @Override
    public SlashCommandEventMessageExecutor returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return slashCommandEvent.getChannel();
    }

    @Override
    public Message getMessage() {
        return overrideMessage;
    }

    /**
     * Returns the {@link ReplyAction} right away instead of queuing everything up for the end. Good if you need
     * access to the {@link ReplyAction#queue()} result.
     *
     * @param message the {@link Message} being sent
     * @return the {@link ReplyAction}
     */
    public ReplyAction replyMessage(Message message) {
        return slashCommandEvent.reply(message);
    }

    public void deferEphemeralReply() {
        slashCommandEvent.deferReply(true).queue();
    }
}
