package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.TestUtils;
import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.MessageAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class CountdownCommandTest {

    @Mock
    public MessageReceivedEvent messageEvent;

    @Mock
    public Message message;

    @Mock
    CountdownRepository countdownRepository;

    @InjectMocks
    CountdownCommand countdownCommand;

    @Test
    void testHelpCommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot countdown");

        MessageAction messageAction = countdownCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmoji(messageAction)).contains(Emojis.CHECK_MARK);
        assertThat(TestUtils.returnPrivateMessageEmbededName(messageAction)).contains("Countdown Command Help");
    }

    @Test
    void testListCommandNoCountdowns() {
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(message.getContentStripped()).thenReturn("@Ninbot countdown list");

        MessageAction messageAction = countdownCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmbeddedTitle(messageAction)).isEqualTo("No countdowns are currently scheduled, "
                + "use \"@Ninbot countdown\" to add your own!");
    }

    @Test
    void testListCommandHasCountdowns() {
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(message.getContentStripped()).thenReturn("@Ninbot countdown list");

        MessageAction messageAction = countdownCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmbeddedTitle(messageAction)).isEqualTo("No countdowns are currently scheduled, "
                + "use \"@Ninbot countdown\" to add your own!");
    }
}