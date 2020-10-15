package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.components.common.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class ListCommandTest {

    @Mock
    static List<String> roleDenyList = new ArrayList<>();
    @Mock
    public MessageReceivedEvent messageEvent;
    @Mock
    public Message message;
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
        roleDenyList.add("admin");
    }

    @Test
    void testList() {
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