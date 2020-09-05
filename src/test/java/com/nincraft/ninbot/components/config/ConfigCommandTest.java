package com.nincraft.ninbot.components.config;

import com.nincraft.ninbot.NinbotRunner;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.MessageAction;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
public class ConfigCommandTest {

    @InjectMocks
    ConfigCommand configCommand;

    @Mock
    Guild guild;

    @Mock
    ConfigService configService;

    @Mock
    public MessageReceivedEvent messageEvent;

    @Mock
    public Message message;

    @Test
    public void testUnknownSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config test");

        MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.QUESTION_MARK);
    }

    @Test
    public void testAddSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config add name value");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");

        MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        verify(configService).addConfig(new Config("1", "name", "value"));
        assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.CHECK_MARK);
    }

    @Test
    public void testAddWrongLengthSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config add name");

        MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.CROSS_X);
    }

    @Test
    public void testRemoveWrongLengthSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config remove name");

        MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.CROSS_X);
    }

    @Test
    public void testRemoveSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config remove name value");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");

        MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        verify(configService).removeConfig(new Config("1", "name", "value"));
        assertThat(TestUtils.returnEmoji(actualMessageAction)).contains(Emojis.CHECK_MARK);
    }

    @Test
    public void testListNoConfigsSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getName()).thenReturn("Test Server");

        MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnMessage(actualMessageAction)).contains("Test Server");
    }

    @Test
    public void testListSubcommand() {
        List<Config> configList = new ArrayList<>();
        configList.add(new Config("1", "name", "value"));
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getName()).thenReturn("Test Server");
        when(configService.getConfigsByServerId("1")).thenReturn(configList);

        MessageAction actualMessageAction = configCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmbeddedTitle(actualMessageAction)).contains("Test Server");
        assertThat(TestUtils.returnEmbeddedName(actualMessageAction)).isEqualToIgnoringCase("name");
        assertThat(TestUtils.returnEmbeddedValue(actualMessageAction)).isEqualToIgnoringCase("value");
    }
}