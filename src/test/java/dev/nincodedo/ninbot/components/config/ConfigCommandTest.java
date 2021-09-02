package dev.nincodedo.ninbot.components.config;

import dev.nincodedo.ninbot.NinbotRunner;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class ConfigCommandTest {

    @Mock
    public MessageReceivedEvent messageEvent;
    @Mock
    public Message message;
    @InjectMocks
    ConfigCommand configCommand;
    @Mock
    Guild guild;
    @Mock
    ConfigService configService;

    @Test
    void testUnknownSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config test");

        //MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        //assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.QUESTION_MARK);
    }

    @Test
    void testAddSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config add name value");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");

        //MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        //verify(configService).addConfig(new Config("1", "name", "value"));
        //assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.CHECK_MARK);
    }

    @Test
    void testAddWrongLengthSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config add name");

        //MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        //assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.CROSS_X);
    }

    @Test
    void testRemoveWrongLengthSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config remove name");

        //MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        //assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.CROSS_X);
    }

    @Test
    void testRemoveSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config remove name value");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");

        //MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        //verify(configService).removeConfig(new Config("1", "name", "value"));
        //assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.CHECK_MARK);
    }

    @Test
    void testListNoConfigsSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getName()).thenReturn("Test Server");

        //MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        //assertThat(TestUtils.returnMessage(actualMessageAction)).contains("Test Server");
    }

    @Test
    void testListSubcommand() {
        List<Config> configList = new ArrayList<>();
        configList.add(new Config("1", "name", "value"));
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getName()).thenReturn("Test Server");
        when(configService.getConfigsByServerId("1")).thenReturn(configList);

        //MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        //assertThat(TestUtils.returnEmbeddedTitle(actualMessageAction)).contains("Test Server");
        //assertThat(TestUtils.returnEmbeddedName(actualMessageAction)).isEqualToIgnoringCase("name");
        //assertThat(TestUtils.returnEmbeddedValue(actualMessageAction)).isEqualToIgnoringCase("value");
    }
}
