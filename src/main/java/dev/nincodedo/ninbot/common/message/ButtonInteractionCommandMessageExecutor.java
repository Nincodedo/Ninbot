package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collections;
import java.util.List;

public class ButtonInteractionCommandMessageExecutor extends EphemeralMessageExecutor {

    private MessageEditData messageEdit;
    private ButtonInteractionEvent buttonInteractionEvent;

    public ButtonInteractionCommandMessageExecutor(ButtonInteractionEvent buttonInteractionEvent) {
        super();
        this.buttonInteractionEvent = buttonInteractionEvent;
    }

    @Override
    public void executeMessageActions() {
        super.executeMessageActions();
        if (messageEdit != null) {
            buttonInteractionEvent.editMessage(messageEdit).queue();
        }
    }

    @Override
    protected ReplyCallbackAction replyMessage(MessageCreateData message) {
        return buttonInteractionEvent.reply(message);
    }

    public ButtonInteractionCommandMessageExecutor editEphemeralMessage(String message) {
        messageEdit = new MessageEditBuilder().setContent(message).build();
        return this;
    }

    @Override
    protected ReplyCallbackAction replyEmbeds(List<MessageEmbed> messageEmbeds) {
        return buttonInteractionEvent.replyEmbeds(messageEmbeds);
    }

    @Override
    public MessageChannel getChannel() {
        return buttonInteractionEvent.getChannel();
    }

    @Override
    public Message getMessage() {
        return buttonInteractionEvent.getMessage();
    }

    public void clearComponents() {
        messageEdit = new MessageEditBuilder().applyData(messageEdit).setComponents(Collections.emptyList()).build();
    }
}
