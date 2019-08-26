package com.nincraft.ninbot.components.config;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigCommand;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.Test;
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
        CommandResult commandResult = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(commandResult)).contains(Emojis.QUESTION_MARK);
    }

    @Test
    public void testAddSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config add name value");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        CommandResult commandResult = configCommand.executeCommand(messageEvent);
        verify(configService).addConfig(new Config("1", "name", "value"));
        assertThat(TestUtils.returnEmoji(commandResult)).contains(Emojis.CHECK_MARK);
    }

    @Test
    public void testAddWrongLengthSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config add name");
        CommandResult commandResult = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(commandResult)).contains(Emojis.CROSS_X);
    }

    @Test
    public void testRemoveWrongLengthSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config remove name");
        CommandResult commandResult = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(commandResult)).contains(Emojis.CROSS_X);
    }

    @Test
    public void testRemoveSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config remove name value");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        CommandResult commandResult = configCommand.executeCommand(messageEvent);
        verify(configService).removeConfig(new Config("1", "name", "value"));
        assertThat(TestUtils.returnEmoji(commandResult)).contains(Emojis.CHECK_MARK);
    }

    @Test
    public void testListNoConfigsSubcommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot config list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getName()).thenReturn("Test Server");
        CommandResult commandResult = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnMessage(commandResult)).contains("Test Server");
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
        CommandResult commandResult = configCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmbeddedTitle(commandResult)).contains("Test Server");
        assertThat(TestUtils.returnEmbeddedName(commandResult)).isEqualToIgnoringCase("name");
        assertThat(TestUtils.returnEmbeddedValue(commandResult)).isEqualToIgnoringCase("value");
    }
}