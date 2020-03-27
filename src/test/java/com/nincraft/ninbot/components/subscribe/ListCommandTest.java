package com.nincraft.ninbot.components.subscribe;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.EnumSet;
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
        JDA jda = Mockito.mock(JDA.class);
        List<Role> roles = new ArrayList<>();
        Role role = Mockito.mock(Role.class);
        when(role.getName()).thenReturn("best");
        when(role.getPermissions()).thenReturn(EnumSet.noneOf(Permission.class));
        roles.add(role);
        when(messageEvent.getMessage()).thenReturn(message);
        when(messageEvent.getGuild()).thenReturn(mockGuild);
        when(message.getContentStripped()).thenReturn("@Ninbot list");
        when(mockGuild.getRoles()).thenReturn(roles);
        MessageAction messageAction = listCommand.executeCommand(messageEvent);
        assertThat(messageAction.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getDescription()
                .trim()).isEqualToIgnoringCase("best");
    }
}