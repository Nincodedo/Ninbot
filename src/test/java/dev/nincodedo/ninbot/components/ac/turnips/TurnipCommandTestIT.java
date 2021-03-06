package dev.nincodedo.ninbot.components.ac.turnips;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.TestUtils;
import dev.nincodedo.ninbot.components.ac.Villager;
import dev.nincodedo.ninbot.components.ac.VillagerManager;
import dev.nincodedo.ninbot.components.ac.VillagerRepository;
import dev.nincodedo.ninbot.components.common.Emojis;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = NinbotRunner.class, initializers = {TurnipCommandTestIT.Initializer.class})
@TestPropertySource("classpath:application.properties")
@Testcontainers
class TurnipCommandTestIT {

    @Container
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql");
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
    TurnipPricesManager turnipPricesManager;
    @Autowired
    TurnipCommand turnipCommand;

    @Test
    void executeHelpCommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips");

        val commandResults = turnipCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnPrivateMessageEmbededName(commandResults)).contains("Turnips Command Help");
    }

    @Test
    void executeJoinCommand() {
        User user = Mockito.mock(User.class);
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips join");
        when(messageEvent.getAuthor()).thenReturn(user);
        when(user.getId()).thenReturn("1");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");

        val commandResults = turnipCommand.executeCommand(messageEvent);
        val villagers = villagerRepository.findAll();

        assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CHECK_MARK);
        assertThat(villagers).isNotEmpty();
    }

    @Test
    void executeBuyCommandOnWeekday() {
        Villager villager = getVillager("2", "2", 0, 2000);
        turnipPricesManager.generateNewWeek();
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips buy 10");
        Clock weekdayClock = Clock.fixed(Instant.parse("2020-09-07T12:00:00.00Z"), ZoneId.systemDefault());
        turnipCommand.setClock(weekdayClock);

        val before = villagerRepository.findByDiscordId("2").get();
        val commandResults = turnipCommand.executeCommand(messageEvent);
        val after = villagerRepository.findByDiscordId("2").get();

        assertThat(after.getTurnipsOwned()).isEqualTo(before.getTurnipsOwned());
        assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CROSS_X);
    }

    private Villager getVillager(String discordId, String serverId, int turnipsOwned, int bells) {
        val optional = villagerRepository.findByDiscordId(discordId);
        Villager villager;
        villager = optional.orElseGet(Villager::new);
        villager.setDiscordId(discordId);
        villager.setDiscordServerId(serverId);
        villager.setTurnipsOwned(turnipsOwned);
        villager.setBellsTotal(bells);
        villagerRepository.save(villager);
        return villager;
    }

    @Test
    void executeBuyCommandOnSunday() {
        Villager villager = getVillager("2", "2", 0, 2000);
        turnipPricesManager.generateNewWeek();
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips buy 10");
        User author = Mockito.mock(User.class);
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getAuthor()).thenReturn(author);
        when(author.getId()).thenReturn("2");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getIdLong()).thenReturn(1L);
        Clock sundayClock = Clock.fixed(Instant.parse("2020-09-06T12:00:00.00Z"), ZoneId.systemDefault());
        turnipCommand.setClock(sundayClock);

        val before = villagerRepository.findByDiscordId("2").get();
        val commandResults = turnipCommand.executeCommand(messageEvent);
        val after = villagerRepository.findByDiscordId("2").get();

        assertThat(after.getTurnipsOwned()).isEqualTo(before.getTurnipsOwned() + 10);
        assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CHECK_MARK);
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