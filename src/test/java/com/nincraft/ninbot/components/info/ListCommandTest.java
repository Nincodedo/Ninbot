package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.entities.RoleImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ListCommandTest extends NinbotTest {

    @Mock
    static List<String> roleBlackList = new ArrayList<>();

    @Mock
    MessageChannel mockMessageChannel;

    @Mock
    Guild mockGuild;

    @Mock
    ConfigService configService;

    @InjectMocks
    ListCommand listCommand;

    @BeforeAll
    public static void setup() {
        roleBlackList.add("admin");
    }

    @Test
    public void testList() {
        List<Role> roles = new ArrayList<>();
        RoleImpl role = new RoleImpl(1L, mockGuild);
        role.setName("best");
        roles.add(role);
        when(messageEvent.getMessage()).thenReturn(message);
        when(messageEvent.getGuild()).thenReturn(mockGuild);
        when(message.getContentStripped()).thenReturn("@Ninbot list");
        when(mockGuild.getRoles()).thenReturn(roles);
        CommandResult commandResult = listCommand.executeCommand(messageEvent);
        assertThat(commandResult.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getDescription()
                .trim()).isEqualToIgnoringCase("best");
    }
}