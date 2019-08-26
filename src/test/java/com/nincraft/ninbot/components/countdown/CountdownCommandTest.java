package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.Emojis;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CountdownCommandTest extends NinbotTest {

    @Mock
    CountdownRepository countdownRepository;

    @InjectMocks
    CountdownCommand countdownCommand;

    @Test
    public void testHelpCommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot countdown");
        CommandResult commandResult = countdownCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(commandResult)).contains(Emojis.CHECK_MARK);
        assertThat(TestUtils.returnPrivateMessageEmbededName(commandResult)).contains("Countdown Command Help");
    }

    @Test
    public void testListCommandNoCountdowns() {
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(message.getContentStripped()).thenReturn("@Ninbot countdown list");
        CommandResult commandResult = countdownCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmbeddedTitle(commandResult)).isEqualTo("No countdowns are currently scheduled, "
                + "use \"@Ninbot countdown\" to add your own!");
    }

    @Test
    public void testListCommandHasCountdowns() {
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(message.getContentStripped()).thenReturn("@Ninbot countdown list");
        CommandResult commandResult = countdownCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmbeddedTitle(commandResult)).isEqualTo("No countdowns are currently scheduled, "
                + "use \"@Ninbot countdown\" to add your own!");
    }
}