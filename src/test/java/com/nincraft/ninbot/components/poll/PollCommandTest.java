package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.components.common.Emojis;
import lombok.val;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PollCommandTest extends NinbotTest {

    @InjectMocks
    PollCommand pollCommand;

    @Test
    void executePollCommand() {
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        MessageChannel messageChannel = Mockito.mock(MessageChannel.class);
        MessageAction messageAction = Mockito.mock(MessageAction.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot poll test \"1, 2, 3\" 5");
        when(messageEvent.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.getAvatarUrl()).thenReturn("http://google.com/a-url");
        when(messageEvent.getChannel()).thenReturn(messageChannel);
        when(messageChannel.sendMessage(any(Message.class))).thenReturn(messageAction);
        val actualMessageAction = pollCommand.executeCommand(messageEvent);
        assertThat(actualMessageAction).isNotNull();
        assertThat(actualMessageAction.getEmojisList()).isEmpty();
        verify(messageAction).queue(any(PollConsumer.class));
    }

    @Test
    void executePollTooManyOptionsCommand() {
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        MessageChannel messageChannel = Mockito.mock(MessageChannel.class);
        MessageAction messageAction = Mockito.mock(MessageAction.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot poll test \"1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11\"");
        when(messageEvent.getMember()).thenReturn(member);
        val actualMessageAction = pollCommand.executeCommand(messageEvent);
        assertThat(actualMessageAction).isNotNull();
        assertThat(actualMessageAction.getEmojisList()).contains(Emojis.CROSS_X);
    }

    @Test
    void parsePollMessage() {
        Member member = Mockito.mock(Member.class);
        when(message.getContentStripped()).thenReturn("@Ninbot poll test \"1, 2, 3\" 10");
        val poll = pollCommand.parsePollMessage(message, member);
        assertThat(poll.getChoices()).isNotEmpty();
        assertThat(poll.getChoices()).hasSize(3);
        assertThat(poll.getTitle()).isEqualTo("test");
        assertThat(poll.getTimeLength()).isEqualTo(10L);
    }
}