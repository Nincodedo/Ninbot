package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandEventMessageExecutor extends MessageExecutor<SlashCommandEventMessageExecutor> {

    private SlashCommandEvent slashCommandEvent;
    private List<Message> ephemeralMessageResponses;

    public SlashCommandEventMessageExecutor(SlashCommandEvent slashCommandEvent) {
        super();
        this.slashCommandEvent = slashCommandEvent;
        this.ephemeralMessageResponses = new ArrayList<>();
    }

    public void executeMessageActions() {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageResponses.forEach(messageBuilder::append);
        slashCommandEvent.reply(messageBuilder.build()).queue();
        MessageBuilder ephemeralMessageBuilder = new MessageBuilder();
        ephemeralMessageResponses.forEach(ephemeralMessageBuilder::append);
        slashCommandEvent.reply(ephemeralMessageBuilder.build()).setEphemeral(true).queue();
    }

    public SlashCommandEventMessageExecutor addEphemeralMessage(Message message) {
        ephemeralMessageResponses.add(message);
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
}
