package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.TestUtils;
import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
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

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class EventCommandTest {

    @Mock
    static
    ConfigService configService;
    @InjectMocks
    static
    EventCommand eventCommand;
    @Mock
    static
    EventRepository eventRepository;
    @Mock
    static
    EventScheduler eventScheduler;
    @Mock
    public MessageReceivedEvent messageEvent;

    @Mock
    public Message message;

    @BeforeAll
    static void setup() {
        eventCommand = new EventCommand(eventRepository, eventScheduler);
        eventCommand.setConfigService(configService);
    }

    @Test
    void listCommandWithEvents() {
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot events list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        List<Event> eventList = new ArrayList<>();
        Event event = new Event();
        event.setName("Event Name");
        event.setStartTime(Instant.now().atZone(ZoneId.of("GMT")));
        eventList.add(event);
        when(eventRepository.findAll()).thenReturn(eventList);

        MessageAction messageAction = eventCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmbeddedTitle(messageAction)).isEqualTo("Current scheduled events");
    }

    @Test
    void listCommandWithoutEvents() {
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot events list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");

        MessageAction messageAction = eventCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnMessage(messageAction)).isEqualTo("No events scheduled");
    }

    @Test
    void planCommand() {
        Guild guild = Mockito.mock(Guild.class);
        User user = Mockito.mock(User.class);
        JDA jda = Mockito.mock(JDA.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getGuild()).thenReturn(guild);
        when(message.getContentStripped()).thenReturn("@Ninbot events plan \"Name\" 2019-01-31T12:00:00-06:00 "
                + "mariokart");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(messageEvent.getAuthor()).thenReturn(user);
        when(messageEvent.getJDA()).thenReturn(jda);

        MessageAction messageAction = eventCommand.executeCommand(messageEvent);

        assertThat(TestUtils.returnEmoji(messageAction)).contains(Emojis.CHECK_MARK);
    }
}