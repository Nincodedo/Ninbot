package dev.nincodedo.nincord.message;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlashCommandEventMessageExecutorTest {

    MessageExecutor messageExecutor;

    @Test
    void executeOneMessageAction() {
        SlashCommandInteractionEvent slashCommandEvent = Mockito.mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = Mockito.mock(ReplyCallbackAction.class);
        when(slashCommandEvent.reply(any(MessageCreateData.class))).thenReturn(replyAction);
        messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        messageExecutor.addMessageResponse("wow");

        messageExecutor.executeActions();

        verify(slashCommandEvent, times(1)).reply(any(MessageCreateData.class));
    }

    @Test
    void executeFiveMessageAction() {
        SlashCommandInteractionEvent slashCommandEvent = Mockito.mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = Mockito.mock(ReplyCallbackAction.class);
        when(slashCommandEvent.reply(any(MessageCreateData.class))).thenReturn(replyAction);
        messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        messageExecutor.addMessageResponse("wow");
        messageExecutor.addMessageResponse("wooow");
        messageExecutor.addMessageResponse("wooooow");
        messageExecutor.addMessageResponse("wooow");
        messageExecutor.addMessageResponse("wow");

        messageExecutor.executeActions();

        verify(slashCommandEvent, times(5)).reply(any(MessageCreateData.class));
    }
}
