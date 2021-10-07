package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SlashCommandEventMessageExecutorTest {

    MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor;

    @Test
    void executeOneMessageAction() {
        SlashCommandEvent slashCommandEvent = Mockito.mock(SlashCommandEvent.class);
        ReplyAction replyAction = Mockito.mock(ReplyAction.class);
        when(slashCommandEvent.reply(any(Message.class))).thenReturn(replyAction);
        messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        messageExecutor.addMessageResponse("wow");

        messageExecutor.executeActions();

        verify(slashCommandEvent, times(1)).reply(any(Message.class));
    }

    @Test
    void executeFiveMessageAction() {
        SlashCommandEvent slashCommandEvent = Mockito.mock(SlashCommandEvent.class);
        ReplyAction replyAction = Mockito.mock(ReplyAction.class);
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