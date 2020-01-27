package com.nincraft.ninbot.components.fun.haiku;


import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.components.config.component.ComponentService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HaikuListenerTest extends NinbotTest {

    @Mock
    ComponentService componentService;

    @InjectMocks
    HaikuListener haikuListener;

    @Test
    public void messageTooShortToHaiku() {
        Guild guild = Mockito.mock(Guild.class);
        TextChannel channel = Mockito.mock(TextChannel.class);
        User user = Mockito.mock(User.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped())
                .thenReturn("too short");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(componentService.isDisabled("haiku","1")).thenReturn(false);
        when(messageEvent.isFromGuild()).thenReturn(true);
        when(messageEvent.getAuthor()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        haikuListener.onMessageReceived(messageEvent);
        verifyNoInteractions(channel);
    }

    @Test
    public void messageHaikuable() {
        String bestHaiku = "the the the the the the the the the the the the the the the the the";
        Guild guild = Mockito.mock(Guild.class);
        TextChannel channel = Mockito.mock(TextChannel.class);
        User user = Mockito.mock(User.class);
        Member member = Mockito.mock(Member.class);
        MessageAction action = Mockito.mock(MessageAction.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped())
                .thenReturn(bestHaiku);
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(componentService.isDisabled("haiku","1")).thenReturn(false);
        when(messageEvent.isFromGuild()).thenReturn(true);
        when(messageEvent.getAuthor()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(messageEvent.getMember()).thenReturn(member);
        when(messageEvent.getChannel()).thenReturn(channel);
        when(channel.sendMessage(any(Message.class))).thenReturn(action);
        haikuListener.onMessageReceived(messageEvent);
        assertThat(haikuListener.isHaikuable(bestHaiku).isPresent()).isTrue();
    }
}