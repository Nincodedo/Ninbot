package dev.nincodedo.ninbot.components.numbers;

import dev.nincodedo.nincord.config.db.Config;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.stats.StatManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoodNumbersListenerTest {

    private static final List<Config> CONFIGS = List.of(new Config("1", "goodNumbers", "69"), new Config("1",
            "goodNumbers", "420"));
    @Mock
    ConfigService configService;

    @Mock
    StatManager statManager;

    @Mock
    ComponentService componentService;

    @Mock
    ExecutorService executorService;

    @InjectMocks
    GoodNumbersListener goodNumbersListener;

    static List<String> goodNumbers() {
        return List.of("60 FPS and 9 awards", "300 the movie was 120 years ago", "Even 70, a non good number can "
                + "become good with -1");
    }

    static List<String> numbers() {
        return List.of("It's 4:21 AM", "69", "this isn't even numbers");
    }

    @ParameterizedTest
    @MethodSource("goodNumbers")
    void messageWithGoodNumbers(String messageContent) {
        MessageReceivedEvent messageReceivedEvent = Mockito.mock(MessageReceivedEvent.class);
        Message message = Mockito.mock(Message.class);
        MessageCreateAction messageAction = Mockito.mock(MessageCreateAction.class);
        Guild guild = Mockito.mock(Guild.class);
        User user = Mockito.mock(User.class);
        when(messageReceivedEvent.isFromGuild()).thenReturn(true);
        when(messageReceivedEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn(messageContent);
        when(messageReceivedEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(messageReceivedEvent.getAuthor()).thenReturn(user);
        when(message.reply(any(MessageCreateData.class))).thenReturn(messageAction);
        when(configService.getGlobalConfigsByName(ConfigConstants.GOOD_NUMBERS, "1")).thenReturn(CONFIGS);

        goodNumbersListener.onMessageReceived(messageReceivedEvent);

        verify(message).reply(any(MessageCreateData.class));
    }

    @ParameterizedTest
    @MethodSource("numbers")
    void messageWithNumbers(String messageContent) {
        MessageReceivedEvent messageReceivedEvent = Mockito.mock(MessageReceivedEvent.class);
        Message message = Mockito.mock(Message.class);
        Guild guild = Mockito.mock(Guild.class);
        User user = Mockito.mock(User.class);
        when(messageReceivedEvent.isFromGuild()).thenReturn(true);
        when(messageReceivedEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn(messageContent);
        when(messageReceivedEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(messageReceivedEvent.getAuthor()).thenReturn(user);
        when(configService.getGlobalConfigsByName(ConfigConstants.GOOD_NUMBERS, "1")).thenReturn(CONFIGS);

        goodNumbersListener.onMessageReceived(messageReceivedEvent);

        verify(message, Mockito.never()).reply(any(MessageCreateData.class));
    }
}
