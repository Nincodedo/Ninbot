package dev.nincodedo.ninbot.components.reaction.processor;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PreviousAuthorTest {

    PreviousAuthor previousAuthor = new PreviousAuthor();

    @Test
    void process() {
        Message message = Mockito.mock(Message.class);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        MessageHistory.MessageRetrieveAction messageRetrieveAction = Mockito.mock(MessageHistory.MessageRetrieveAction.class);
        MessageHistory messageHistory = Mockito.mock(MessageHistory.class);
        Member member = Mockito.mock(Member.class);
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        ReactionContext reactionContext = new ReactionContext();
        reactionContext.setMessage(message);
        reactionContext.setChannel(channel);
        when(channel.getHistoryBefore(message, 1)).thenReturn(messageRetrieveAction);
        when(messageRetrieveAction.complete()).thenReturn(messageHistory);
        when(messageHistory.getRetrievedHistory()).thenReturn(messages);
        when(message.getContentRaw()).thenReturn("Test");
        when(message.getMember()).thenReturn(member);
        when(member.getEffectiveName()).thenReturn("Nincodedo");
        reactionContext.setReactionMessage("Blah \"$message.previous.author\"");
        previousAuthor.process(reactionContext);
        var reaction = reactionContext.getReactionMessage();

        assertThat(reaction).isEqualTo("Blah \"Nincodedo\"");
    }
}
