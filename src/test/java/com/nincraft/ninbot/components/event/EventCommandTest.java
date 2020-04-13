package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class EventCommandTest extends NinbotTest {

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