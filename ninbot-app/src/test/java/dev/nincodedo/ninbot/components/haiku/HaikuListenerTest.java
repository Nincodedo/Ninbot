package dev.nincodedo.ninbot.components.haiku;


import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.stats.StatManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
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

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HaikuListenerTest {

    @Mock
    public MessageReceivedEvent messageEvent;

    @Mock
    public Message message;

    @Mock
    ComponentService componentService;

    @Mock
    ExecutorService executorService;

    @Mock
    ConfigService configService;

    @Mock
    StatManager statManager;

    @InjectMocks
    MockHaikuListener haikuListener;

    static List<String> nonhaikuables() {
        return List.of("too short", "the the the the the the the the the the the the the the the the 9",
                "Because Amazon continues to be a curse on my work life.");
    }

    static List<String> haikuables() {
        return List.of("the the the the the the the the the the the the the the the the the",
                "Because Amazon continues to be a curse on my whole work life.");
    }

    @ParameterizedTest
    @MethodSource("nonhaikuables")
    void messagesUnhaikuable(String unhaikuable) {
        Guild guild = Mockito.mock(Guild.class);
        User user = Mockito.mock(User.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn(unhaikuable);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(componentService.isDisabled("haiku", "1")).thenReturn(false);
        when(messageEvent.isFromGuild()).thenReturn(true);
        when(messageEvent.getAuthor()).thenReturn(user);
        when(user.isBot()).thenReturn(false);

        haikuListener.onMessageReceived(messageEvent);

        assertThat(haikuListener.isHaikuable(unhaikuable, "1")).isNotPresent();
    }

    @ParameterizedTest
    @MethodSource("haikuables")
    void messageHaikuable(String haikuable) {
        Guild guild = Mockito.mock(Guild.class);
        MessageChannelUnion channel = Mockito.mock(MessageChannelUnion.class);
        User user = Mockito.mock(User.class);
        Member member = Mockito.mock(Member.class);
        MessageCreateAction action = Mockito.mock(MessageCreateAction.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped())
                .thenReturn(haikuable);
        when(message.getContentRaw()).thenReturn(haikuable);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(componentService.isDisabled("haiku", "1")).thenReturn(false);
        when(messageEvent.isFromGuild()).thenReturn(true);
        when(messageEvent.getAuthor()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(messageEvent.getMember()).thenReturn(member);
        when(messageEvent.getChannel()).thenReturn(channel);
        when(channel.sendMessage(any(MessageCreateData.class))).thenReturn(action);

        haikuListener.onMessageReceived(messageEvent);

        assertThat(haikuListener.isHaikuable(haikuable, "1")).isPresent();
    }

    public static class MockHaikuListener extends HaikuListener {

        public MockHaikuListener(ComponentService componentService, StatManager statManager,
                ExecutorService executorService,
                ConfigService configService) {
            super(statManager, executorService, componentService, configService);
        }

        @Override
        boolean checkChance(String guildId) {
            return true;
        }
    }

}
