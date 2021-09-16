package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.NinbotRunner;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class PollCommandTest {

    @Mock
    SlashCommandEvent slashCommandEvent;

    @InjectMocks
    PollCommand pollCommand;

    @Test
    void executePollCommand() {
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        TextChannel textChannel = Mockito.mock(TextChannel.class);
        Guild guild = Mockito.mock(Guild.class);
        ReplyAction replyAction = Mockito.mock(ReplyAction.class);
        OptionMapping choice1 = Mockito.mock(OptionMapping.class);
        OptionMapping choice2 = Mockito.mock(OptionMapping.class);
        OptionMapping questionOption = Mockito.mock(OptionMapping.class);
        when(slashCommandEvent.getTextChannel()).thenReturn(textChannel);
        when(slashCommandEvent.getGuild()).thenReturn(guild);
        when(slashCommandEvent.getMember()).thenReturn(member);
        when(slashCommandEvent.getOption("choice1")).thenReturn(choice1);
        when(slashCommandEvent.getOption("choice2")).thenReturn(choice2);
        when(slashCommandEvent.getOption("question")).thenReturn(questionOption);
        when(slashCommandEvent.reply(any(Message.class))).thenReturn(replyAction);
        when(choice1.getAsString()).thenReturn("1st");
        when(choice2.getAsString()).thenReturn("2nd");
        when(questionOption.getAsString()).thenReturn("why?");
        when(guild.getId()).thenReturn("1");
        when(textChannel.getId()).thenReturn("1");
        when(member.getUser()).thenReturn(user);
        when(user.getAvatarUrl()).thenReturn("http://google.com/a-url");
        when(guild.getLocale()).thenReturn(Locale.ENGLISH);

        pollCommand.execute(slashCommandEvent);

        //assertThat(actualMessageAction).isNotNull();
        //assertThat(actualMessageAction.getEmojisList()).isEmpty();
    }

    @Test
    void parsePollMessage() {
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        TextChannel textChannel = Mockito.mock(TextChannel.class);
        Guild guild = Mockito.mock(Guild.class);
        OptionMapping choice1 = Mockito.mock(OptionMapping.class);
        OptionMapping choice2 = Mockito.mock(OptionMapping.class);
        OptionMapping choice3 = Mockito.mock(OptionMapping.class);
        OptionMapping questionOption = Mockito.mock(OptionMapping.class);
        when(slashCommandEvent.getTextChannel()).thenReturn(textChannel);
        when(slashCommandEvent.getGuild()).thenReturn(guild);
        when(slashCommandEvent.getOption("choice1")).thenReturn(choice1);
        when(slashCommandEvent.getOption("choice2")).thenReturn(choice2);
        when(slashCommandEvent.getOptions()).thenReturn(Arrays.asList(choice3));
        when(choice3.getName()).thenReturn("choice3");
        when(choice3.getAsString()).thenReturn("3rd");
        when(slashCommandEvent.getOption("question")).thenReturn(questionOption);
        when(questionOption.getAsString()).thenReturn("test");
        when(slashCommandEvent.getOption("user-choices")).thenReturn(null);
        when(slashCommandEvent.getOption("poll-length")).thenReturn(null);
        when(guild.getId()).thenReturn("1");
        when(member.getUser()).thenReturn(user);
        when(textChannel.getId()).thenReturn("1");
        when(user.getAvatarUrl()).thenReturn("http://avatarturl.com/avatar.png");
        when(member.getEffectiveName()).thenReturn("Nincodedo");

        var poll = pollCommand.parsePollMessage(slashCommandEvent, member);

        assertThat(poll.getChoices()).isNotEmpty();
        assertThat(poll.getChoices()).hasSize(3);
        assertThat(poll.getTitle()).isEqualTo("test");
    }
}
