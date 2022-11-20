package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.NinbotRunner;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
@MockitoSettings(strictness = Strictness.LENIENT)
class PollCommandTest {

    @Mock
    SlashCommandInteractionEvent slashCommandEvent;

    @InjectMocks
    PollCommand pollCommand;

    @Test
    void executePollCommand() {
        Member member = Mockito.mock(Member.class);
        MessageChannelUnion channelUnion = Mockito.mock(MessageChannelUnion.class);
        Guild guild = Mockito.mock(Guild.class);
        ReplyCallbackAction replyAction = Mockito.mock(ReplyCallbackAction.class);
        when(slashCommandEvent.getChannel()).thenReturn(channelUnion);
        when(slashCommandEvent.getGuild()).thenReturn(guild);
        when(slashCommandEvent.getMember()).thenReturn(member);
        when(slashCommandEvent.getOption(eq("choice1"), any())).thenReturn("1st");
        when(slashCommandEvent.getOption(eq("choice2"), any())).thenReturn("2nd");
        when(slashCommandEvent.getOption(eq("question"), any())).thenReturn("why?");
        when(slashCommandEvent.getOption(eq("polllength"), eq(5L), any())).thenReturn(5L);
        when(slashCommandEvent.reply(any(MessageCreateData.class))).thenReturn(replyAction);
        when(guild.getId()).thenReturn("1");
        when(channelUnion.getId()).thenReturn("1");
        when(member.getEffectiveAvatarUrl()).thenReturn("http://google.com/a-url");
        when(guild.getLocale()).thenReturn(DiscordLocale.ENGLISH_US);

        var actualMessageAction = pollCommand.execute(slashCommandEvent);

        assertThat(actualMessageAction).isNotNull();
        assertThat(actualMessageAction.getReactions()).isEmpty();
    }

    @Test
    void parsePollMessage() {
        Member member = Mockito.mock(Member.class);
        MessageChannelUnion channelUnion = Mockito.mock(MessageChannelUnion.class);
        Guild guild = Mockito.mock(Guild.class);
        OptionMapping choice3 = Mockito.mock(OptionMapping.class);
        when(slashCommandEvent.getChannel()).thenReturn(channelUnion);
        when(slashCommandEvent.getGuild()).thenReturn(guild);
        when(slashCommandEvent.getOption(eq("choice1"), any())).thenReturn("1st");
        when(slashCommandEvent.getOption(eq("choice2"), any())).thenReturn("2nd");
        when(slashCommandEvent.getOptions()).thenReturn(List.of(choice3));
        when(choice3.getName()).thenReturn("choice3");
        when(choice3.getAsString()).thenReturn("3rd");
        when(slashCommandEvent.getOption(eq("question"), any())).thenReturn("test");
        when(slashCommandEvent.getOption("userchoices")).thenReturn(null);
        when(slashCommandEvent.getOption(eq("polllength"), eq(5L), any())).thenReturn(5L);
        when(guild.getId()).thenReturn("1");
        when(channelUnion.getId()).thenReturn("1");
        when(member.getEffectiveAvatarUrl()).thenReturn("http://avatarturl.com/avatar.png");
        when(member.getEffectiveName()).thenReturn("Nincodedo");

        var poll = pollCommand.parsePollMessage(slashCommandEvent, member);

        assertThat(poll.getChoices()).isNotEmpty();
        assertThat(poll.getChoices()).hasSize(3);
        assertThat(poll.getTitle()).isEqualTo("test");
    }
}
