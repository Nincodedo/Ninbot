package dev.nincodedo.ninbot.components.reaction.processor;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PreviousContentTest {

    PreviousContent previousContent = new PreviousContent();

    @Test
    void process() {
        Message message = Mockito.mock(Message.class);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        MessageHistory.MessageRetrieveAction messageRetrieveAction = Mockito.mock(MessageHistory.MessageRetrieveAction.class);
        MessageHistory messageHistory = Mockito.mock(MessageHistory.class);
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        ReactionContext reactionContext = new ReactionContext();
        reactionContext.setMessage(message);
        reactionContext.setChannel(channel);
        when(channel.getHistoryBefore(message, 1)).thenReturn(messageRetrieveAction);
        when(messageRetrieveAction.complete()).thenReturn(messageHistory);
        when(messageHistory.getRetrievedHistory()).thenReturn(messages);
        when(message.getContentRaw()).thenReturn("Test");
        reactionContext.setReactionMessage("Blah \"$message.previous.content\"");
        previousContent.process(reactionContext);
        var reaction = reactionContext.getReactionMessage();

        assertThat(reaction).isEqualTo("Blah \"Test\"");
        assertThat(reactionContext.isCanReact()).isTrue();
    }

    @Test
    void processUppercase() {
        Message message = Mockito.mock(Message.class);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        MessageHistory.MessageRetrieveAction messageRetrieveAction = Mockito.mock(MessageHistory.MessageRetrieveAction.class);
        MessageHistory messageHistory = Mockito.mock(MessageHistory.class);
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        ReactionContext reactionContext = new ReactionContext();
        reactionContext.setMessage(message);
        reactionContext.setChannel(channel);
        when(channel.getHistoryBefore(message, 1)).thenReturn(messageRetrieveAction);
        when(messageRetrieveAction.complete()).thenReturn(messageHistory);
        when(messageHistory.getRetrievedHistory()).thenReturn(messages);
        when(message.getContentRaw()).thenReturn("Test");
        reactionContext.setReactionMessage("Blah \"$message.previous.content#toUpper\"");
        previousContent.process(reactionContext);
        var reaction = reactionContext.getReactionMessage();

        assertThat(reaction).isEqualTo("Blah \"TEST\"");
        assertThat(reactionContext.isCanReact()).isTrue();
    }

    @Test
    void processBlank() {
        Message message = Mockito.mock(Message.class);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        MessageHistory.MessageRetrieveAction messageRetrieveAction = Mockito.mock(MessageHistory.MessageRetrieveAction.class);
        MessageHistory messageHistory = Mockito.mock(MessageHistory.class);
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        ReactionContext reactionContext = new ReactionContext();
        reactionContext.setMessage(message);
        reactionContext.setChannel(channel);
        when(channel.getHistoryBefore(message, 1)).thenReturn(messageRetrieveAction);
        when(messageRetrieveAction.complete()).thenReturn(messageHistory);
        when(messageHistory.getRetrievedHistory()).thenReturn(messages);
        when(message.getContentRaw()).thenReturn("");
        reactionContext.setReactionMessage("Blah \"$message.previous.content#toUpper\"");
        previousContent.process(reactionContext);

        assertThat(reactionContext.isCanReact()).isFalse();
    }
}
