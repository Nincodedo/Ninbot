package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.NinbotRunner;
import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    MessageUtils messageUtils;

    @Mock
    ConfigService configService;

    @InjectMocks
    ListCommand listCommand;

    @BeforeClass
    public static void setup() {
        roleBlackList.add("admin");
    }

    @Ignore
    @Test
    public void executeCommand() {
        //listCommand.setMessageUtils(messageUtils);
        List<Role> roles = new ArrayList<>();
        Role role = new RoleImpl(1L, mockGuild);
        roles.add(role);
        when(mockMessageEvent.getChannel()).thenReturn(mockMessageChannel);
        when(mockMessageEvent.getMessage()).thenReturn(mockMessage);
        when(mockMessageEvent.getGuild()).thenReturn(mockGuild);
        when(mockMessage.getContentStripped()).thenReturn("@Ninbot list");
        when(mockGuild.getRoles()).thenReturn(roles);
        listCommand.executeCommand(mockMessageEvent);
        verify(messageUtils, times(1)).sendMessage(any(MessageChannel.class), any(Message.class));
    }
}