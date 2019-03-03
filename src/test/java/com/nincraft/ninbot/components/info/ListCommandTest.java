package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.NinbotRunner;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
public class ListCommandTest {

    @Mock
    static List<String> roleBlackList = new ArrayList<>();

    @Mock
    MessageReceivedEvent mockMessageEvent;

    @Mock
    MessageChannel mockMessageChannel;

    @Mock
    Message mockMessage;

    @Mock
    Guild mockGuild;

    @Mock
    ConfigService configService;

    @InjectMocks
    ListCommand listCommand;

    @BeforeClass
    public static void setup() {
        roleBlackList.add("admin");
    }

    @Test
    public void executeCommand() {
        List<Role> roles = new ArrayList<>();
        RoleImpl role = new RoleImpl(1L, mockGuild);
        role.setName("best");
        roles.add(role);
        when(mockMessageEvent.getMessage()).thenReturn(mockMessage);
        when(mockMessageEvent.getGuild()).thenReturn(mockGuild);
        when(mockMessage.getContentStripped()).thenReturn("@Ninbot list");
        when(mockGuild.getRoles()).thenReturn(roles);
        CommandResult commandResult = listCommand.executeCommand(mockMessageEvent);
        assertEquals("best", commandResult.getChannelMessageList().get(0).getEmbeds().get(0).getDescription().trim());
    }
}