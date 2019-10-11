package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
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
    EventRepository eventRepository;

    @Mock
    ConfigService configService;

    @Mock
    EventScheduler eventScheduler;

    @InjectMocks
    EventCommand eventCommand;

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
        CommandResult commandResult = eventCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmbeddedTitle(commandResult)).isEqualTo("Current scheduled events");
    }

    @Test
    void listCommandWithoutEvents() {
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot events list");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        CommandResult commandResult = eventCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnMessage(commandResult)).isEqualTo("No events scheduled");
    }

    @Test
    void planCommand() {
        Guild guild = Mockito.mock(Guild.class);
        User user = Mockito.mock(User.class);
        JDA jda = Mockito.mock(JDA.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getGuild()).thenReturn(guild);
        when(message.getContentStripped()).thenReturn("@Ninbot events plan \"Name\" 2019-01-31T12:00:00-06:00 mariokart");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(messageEvent.getAuthor()).thenReturn(user);
        when(messageEvent.getJDA()).thenReturn(jda);
        CommandResult commandResult = eventCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(commandResult)).contains(Emojis.CHECK_MARK);
    }
}