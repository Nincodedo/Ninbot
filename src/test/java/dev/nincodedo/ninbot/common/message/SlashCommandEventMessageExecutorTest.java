package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class SlashCommandEventMessageExecutorTest {

    MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor;

    @Test
    void executeOneMessageAction() {
        SlashCommandInteractionEvent slashCommandEvent = Mockito.mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = Mockito.mock(ReplyCallbackAction.class);
        when(slashCommandEvent.reply(any(Message.class))).thenReturn(replyAction);
        messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        messageExecutor.addMessageResponse("wow");

        messageExecutor.executeActions();

        verify(slashCommandEvent, times(1)).reply(any(Message.class));
    }

    @Test
    void executeFiveMessageAction() {
        SlashCommandInteractionEvent slashCommandEvent = Mockito.mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = Mockito.mock(ReplyCallbackAction.class);
        when(slashCommandEvent.reply(any(Message.class))).thenReturn(replyAction);
        messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        messageExecutor.addMessageResponse("wow");
        messageExecutor.addMessageResponse("wooow");
        messageExecutor.addMessageResponse("wooooow");
        messageExecutor.addMessageResponse("wooow");
        messageExecutor.addMessageResponse("wow");

        messageExecutor.executeActions();

        verify(slashCommandEvent, times(5)).reply(any(Message.class));
    }
}