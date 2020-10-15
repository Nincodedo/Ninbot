package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.TestUtils;
import dev.nincodedo.ninbot.components.command.CommandParser;
import dev.nincodedo.ninbot.components.common.RolePermission;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = NinbotRunner.class, initializers = {HelpCommandTestIT.Initializer.class})
@TestPropertySource("classpath:application.properties")
@Log4j2
@Testcontainers
class HelpCommandTestIT {
    @Container
    private static final MySQLContainer mySQLContainer = new MySQLContainer();
    @Mock
    public MessageReceivedEvent messageEvent;
    @Mock
    public Message message;
    @MockBean
    ShardManager shardManager;
    @Autowired
    ComponentService componentService;
    @Autowired
    ConfigService configService;
    @Autowired
    CommandParser commandParser;

    @Test
    void testEveryoneCommandsAllHaveHelpDescriptions() {
        val helpCommand = (HelpCommand) commandParser.getCommandHashMap().get("help");
        Guild guild = Mockito.mock(Guild.class);
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getOwner()).thenReturn(member);
        when(member.getUser()).thenReturn(user);

        val messageAction = helpCommand.executeCommand(messageEvent);

        Assertions.assertThat(messageAction).isNotNull();
        assertThat(messageAction.getPrivateMessageList()).isNotEmpty();
        val everyoneCommands = commandParser.getCommandHashMap()
                .values()
                .stream()
                .filter(abstractCommand -> abstractCommand.getPermissionLevel().equals(RolePermission.EVERYONE))
                .collect(Collectors
                        .toList());
        assertThat(TestUtils.returnPrivateMessageEmbedFields(messageAction)).hasSameSizeAs(everyoneCommands);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mySQLContainer.getUsername(),
                    "spring.datasource.password=" + mySQLContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
