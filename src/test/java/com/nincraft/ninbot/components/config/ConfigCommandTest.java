package com.nincraft.ninbot.components.config;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.MessageAction;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigCommandTest extends NinbotTest {

    @InjectMocks
    ConfigCommand configCommand;

    @Mock
    Guild guild;

    @Mock
    ConfigService configService;

    @Test
    public void testUnknownSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config test");
        MessageAction messageAction = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(messageAction)).contains(Emojis.QUESTION_MARK);
    }

    @Test
    public void testAddSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config add name value");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        MessageAction messageAction = configCommand.executeCommand(messageEvent);
        verify(configService).addConfig(new Config("1", "name", "value"));
        assertThat(TestUtils.returnEmoji(messageAction)).contains(Emojis.CHECK_MARK);
    }

    @Test
    public void testAddWrongLengthSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config add name");
        MessageAction messageAction = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(messageAction)).contains(Emojis.CROSS_X);
    }

    @Test
    public void testRemoveWrongLengthSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config remove name");
        MessageAction messageAction = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(messageAction)).contains(Emojis.CROSS_X);
    }

    @Test
    public void testRemoveSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config remove name value");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        MessageAction messageAction = configCommand.executeCommand(messageEvent);
        verify(configService).removeConfig(new Config("1", "name", "value"));
        assertThat(TestUtils.returnEmoji(messageAction)).contains(Emojis.CHECK_MARK);
    }

    @Test
    public void testListNoConfigsSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getName()).thenReturn("Test Server");
        MessageAction messageAction = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnMessage(messageAction)).contains("Test Server");
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
        MessageAction messageAction = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmbeddedTitle(messageAction)).contains("Test Server");
        assertThat(TestUtils.returnEmbeddedName(messageAction)).isEqualToIgnoringCase("name");
        assertThat(TestUtils.returnEmbeddedValue(messageAction)).isEqualToIgnoringCase("value");
    }
}