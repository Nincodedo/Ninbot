package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.components.config.component.ComponentService;
import com.nincraft.ninbot.components.simulate.SimulateCommand;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class HelpCommandTest extends NinbotTest {

    static Map<String, AbstractCommand> commandMap;

    @Mock
    ComponentService componentService;

    @Mock
    ConfigService configService;

    @InjectMocks
    HelpCommand helpCommand = new HelpCommand(commandMap, componentService, configService);

    @BeforeAll
    public static void before() {
        commandMap = new HashMap<>();
        val simulateCommand = new SimulateCommand();
        commandMap.put(simulateCommand.getName(), simulateCommand);
    }

    @Test
    void executeHelpCommand() {
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(componentService.isDisabled(anyString(), anyString())).thenReturn(false);
        val messageAction = helpCommand.executeCommand(messageEvent);
        assertThat(messageAction).isNotNull();
        assertThat(messageAction.getPrivateMessageList()).isNotEmpty();
    }

    @Test
    void executeHelpWithMissingCommand() {
        commandMap.put("test", new TestCommand());
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(componentService.isDisabled(anyString(), anyString())).thenReturn(false);
        val messageAction = helpCommand.executeCommand(messageEvent);
        assertThat(messageAction).isNotNull();
        assertThat(messageAction.getPrivateMessageList()).isNotEmpty();
        assertThat(TestUtils.returnPrivateMessageEmbedFields(messageAction)).hasSize(1);
    }

    private class TestCommand extends AbstractCommand {
        @Override
        public MessageAction executeCommand(
                MessageReceivedEvent event) {
            return null;
        }
    }
}