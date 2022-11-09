package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SlashCommandEventMessageExecutor extends EphemeralMessageExecutor {

    private SlashCommandInteractionEvent slashCommandEvent;

    public SlashCommandEventMessageExecutor(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        super();
        this.slashCommandEvent = slashCommandEvent;
    }

    @Override
    public MessageChannel getChannel() {
        return slashCommandEvent.getChannel();
    }

    @Override
    public Message getMessage() {
        return overrideMessage;
    }

    @Override
    protected ModalCallbackAction replyModal(Modal modal) {
        return slashCommandEvent.replyModal(modal);
    }

    public ReplyCallbackAction replyMessage(MessageCreateData message) {
        return slashCommandEvent.reply(message);
    }

    @Override
    protected ReplyCallbackAction replyEmbeds(List<MessageEmbed> messageEmbeds) {
        return slashCommandEvent.replyEmbeds(messageEmbeds);
    }

    public void deferEphemeralReply() {
        slashCommandEvent.deferReply(true).queue();
    }
}
