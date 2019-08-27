package com.nincraft.ninbot.components.ac.turnips;

import com.nincraft.ninbot.NinbotRunner;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.ac.VillagerManager;
import com.nincraft.ninbot.components.ac.VillagerRepository;
import com.nincraft.ninbot.components.common.Emojis;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.ClassRule;
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

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = NinbotRunner.class, initializers = {TurnipCommandTest.Initializer.class})
@TestPropertySource("classpath:application.properties")
@Log4j2
public class TurnipCommandTest {

    @ClassRule
    public static MySQLContainer mySQLContainer = new MySQLContainer();

    static {
        mySQLContainer.start();
    }

    @Mock
    public MessageReceivedEvent messageEvent;
    @Mock
    public Message message;
    @MockBean
    ShardManager shardManager;
    @Autowired
    VillagerRepository villagerRepository;
    @Autowired
    VillagerManager villagerManager;
    @Autowired
    TurnipCommand turnipCommand;

    @Test
    public void executeHelpCommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips");
        val commandResults = turnipCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnPrivateMessageEmbededName(commandResults)).contains("Turnips Command Help");
    }

    @Test
    public void executeJoinCommand() {
        User user = Mockito.mock(User.class);
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips join");
        when(messageEvent.getAuthor()).thenReturn(user);
        when(user.getId()).thenReturn("1");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        val commandResults = turnipCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CHECK_MARK);
        val villagers = villagerRepository.findAll();
        villagers.forEach(villager -> log.info("VILLAGER: " + villager));
        assertThat(villagers).isNotEmpty();
    }

    @Test
    public void executeBuyCommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips buy 10");
        val commandResults = turnipCommand.executeCommand(messageEvent);
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CHECK_MARK);
        } else {
            assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CROSS_X);
        }
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
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