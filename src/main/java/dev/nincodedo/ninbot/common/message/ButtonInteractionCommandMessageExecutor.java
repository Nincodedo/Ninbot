package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.List;

public class ButtonInteractionCommandMessageExecutor extends EphemeralMessageExecutor<ButtonInteractionCommandMessageExecutor> {

    private MessageEditData messageEdit;
    private static final String CREATE_MESSAGE_EXCEPTION = "Button interactions can't create new messages, use edit "
            + "instead";
    private ButtonInteractionEvent buttonInteractionEvent;

    public ButtonInteractionCommandMessageExecutor(ButtonInteractionEvent buttonInteractionEvent) {
        super();
        this.buttonInteractionEvent = buttonInteractionEvent;
    }

    @Override
    public void executeMessageActions() {
        buttonInteractionEvent.editMessage(messageEdit).queue();
    }

    @Contract("_ -> fail")
    @Override
    protected ReplyCallbackAction replyMessage(MessageCreateData message) {
        throw new IllegalCallerException(CREATE_MESSAGE_EXCEPTION);
    }

    @Contract("_ -> fail")
    @Override
    protected ReplyCallbackAction replyEphemeralMessage(MessageCreateData message) {
        throw new IllegalCallerException(CREATE_MESSAGE_EXCEPTION);
    }

    @Contract("_ -> fail")
    @Override
    public ButtonInteractionCommandMessageExecutor addEphemeralMessage(MessageCreateData message) {
        throw new IllegalCallerException(CREATE_MESSAGE_EXCEPTION);
    }

    public ButtonInteractionCommandMessageExecutor editEphemeralMessage(MessageEditData message) {
        messageEdit = message;
        return this;
    }

    public ButtonInteractionCommandMessageExecutor editEphemeralMessage(String message) {
        messageEdit = new MessageEditBuilder().setContent(message).build();
        return this;
    }

    @Override
    protected ReplyCallbackAction replyEmbeds(List<MessageEmbed> messageEmbeds) {
        return null;
    }

    @Override
    public ButtonInteractionCommandMessageExecutor returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return buttonInteractionEvent.getChannel();
    }

    @Override
    public Message getMessage() {
        return buttonInteractionEvent.getMessage();
    }

    public ButtonInteractionCommandMessageExecutor clearComponents() {
        messageEdit = new MessageEditBuilder().applyData(messageEdit).setComponents(Collections.emptyList()).build();
        return returnThis();
    }
}
