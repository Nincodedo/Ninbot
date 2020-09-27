package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.LocaleService;
import lombok.val;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class PollCommandTest {

    @Mock
    public MessageReceivedEvent messageEvent;

    @Mock
    public Message message;

    @Mock
    LocaleService localeService;

    @InjectMocks
    PollCommand pollCommand;

    @Test
    void executePollCommand() {
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        MessageChannel messageChannel = Mockito.mock(MessageChannel.class);
        MessageAction messageAction = Mockito.mock(MessageAction.class);
        TextChannel textChannel = Mockito.mock(TextChannel.class);
        Guild guild = Mockito.mock(Guild.class);
        when(message.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        pollCommand.setLocaleService(localeService);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getTextChannel()).thenReturn(textChannel);
        when(textChannel.getId()).thenReturn("1");
        when(message.getContentStripped()).thenReturn("@Ninbot poll test \"1, 2, 3\" 5");
        when(messageEvent.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.getAvatarUrl()).thenReturn("http://google.com/a-url");
        when(messageEvent.getChannel()).thenReturn(messageChannel);
        when(messageChannel.sendMessage(any(Message.class))).thenReturn(messageAction);
        when(localeService.getLocale(messageEvent)).thenReturn(Locale.ENGLISH);

        val actualMessageAction = pollCommand.executeCommand(messageEvent);

        assertThat(actualMessageAction).isNotNull();
        assertThat(actualMessageAction.getEmojisList()).isEmpty();
    }

    @Test
    void executePollTooManyOptionsCommand() {
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        MessageChannel messageChannel = Mockito.mock(MessageChannel.class);
        MessageAction messageAction = Mockito.mock(MessageAction.class);
        TextChannel textChannel = Mockito.mock(TextChannel.class);
        Guild guild = Mockito.mock(Guild.class);
        when(message.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        pollCommand.setLocaleService(localeService);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getTextChannel()).thenReturn(textChannel);
        when(textChannel.getId()).thenReturn("1");
        when(message.getContentStripped()).thenReturn("@Ninbot poll test \"1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11\"");
        when(messageEvent.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.getAvatarUrl()).thenReturn("http://avatarturl.com/avatar.png");
        when(member.getEffectiveName()).thenReturn("Nincodedo");
        when(localeService.getLocale(messageEvent)).thenReturn(Locale.ENGLISH);

        val actualMessageAction = pollCommand.executeCommand(messageEvent);

        assertThat(actualMessageAction).isNotNull();
        assertThat(actualMessageAction.getEmojisList()).contains(Emojis.CROSS_X);
    }

    @Test
    void parsePollMessage() {
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        TextChannel textChannel = Mockito.mock(TextChannel.class);
        Guild guild = Mockito.mock(Guild.class);
        when(message.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(member.getUser()).thenReturn(user);
        when(textChannel.getId()).thenReturn("1");
        when(message.getTextChannel()).thenReturn(textChannel);
        when(user.getAvatarUrl()).thenReturn("http://avatarturl.com/avatar.png");
        when(member.getEffectiveName()).thenReturn("Nincodedo");
        when(message.getContentStripped()).thenReturn("@Ninbot poll test \"1, 2, 3\" 10");

        val poll = pollCommand.parsePollMessage(message, member);

        assertThat(poll.getChoices()).isNotEmpty();
        assertThat(poll.getChoices()).hasSize(3);
        assertThat(poll.getTitle()).isEqualTo("test");
        assertThat(poll.getTimeLength()).isEqualTo(10L);
    }
}
