package dev.nincodedo.ninbot.components.dad;

import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DadbotTest {

    @Mock
    ConfigService configService;
    @Mock
    ComponentService componentService;

    @InjectMocks
    Dadbot dadbot;

    @Captor
    private ArgumentCaptor<Consumer> lambdaCaptor;

    @Test
    void oneDadJoke() {
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        Member selfMember = Mockito.mock(Member.class);
        Void voidmock = Mockito.mock(Void.class);
        AuditableRestAction auditableRestAction = Mockito.mock(AuditableRestAction.class);
        String dadJokeName = "tired";

        when(member.getGuild()).thenReturn(guild);
        when(guild.getSelfMember()).thenReturn(selfMember);
        when(selfMember.hasPermission(Permission.NICKNAME_MANAGE)).thenReturn(true);
        when(selfMember.canInteract(member)).thenReturn(true);
        when(guild.getId()).thenReturn("1");
        when(member.getId()).thenReturn("2");
        when(member.getNickname()).thenReturn("Nin");
        when(member.modifyNickname("Tired")).thenReturn(auditableRestAction);
        when(auditableRestAction.reason(anyString())).thenReturn(auditableRestAction);

        when(member.modifyNickname("Nin")).thenReturn(auditableRestAction);

        dadbot.dadJoke(dadJokeName, member);
        verify(member).modifyNickname(anyString());
        assertThat(dadbot.getOriginalNicknames()).isNotEmpty();
        assertThat(dadbot.getOriginalNicknames().get("1-2")).contains("Nin");

        verify(auditableRestAction).queue(lambdaCaptor.capture());

        Consumer<Void> voidConsumer = lambdaCaptor.getValue();
        voidConsumer.accept(voidmock);

        assertThat(dadbot.getOriginalNicknames()).isEmpty();
    }

    @Test
    void multipleDadJokes() {
        Member member = Mockito.mock(Member.class);
        Member secondJoke = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        Member selfMember = Mockito.mock(Member.class);
        Void voidmock = Mockito.mock(Void.class);
        AuditableRestAction auditableRestAction = Mockito.mock(AuditableRestAction.class);

        when(member.getGuild()).thenReturn(guild);
        when(secondJoke.getGuild()).thenReturn(guild);
        when(guild.getSelfMember()).thenReturn(selfMember);
        when(selfMember.hasPermission(Permission.NICKNAME_MANAGE)).thenReturn(true);
        when(selfMember.canInteract(member)).thenReturn(true);
        when(selfMember.canInteract(secondJoke)).thenReturn(true);
        when(guild.getId()).thenReturn("1");
        when(member.getId()).thenReturn("2");
        when(secondJoke.getId()).thenReturn("2");
        when(member.getNickname()).thenReturn("Nin");
        when(secondJoke.getNickname()).thenReturn("Tired");
        when(member.modifyNickname("Tired")).thenReturn(auditableRestAction);
        when(secondJoke.modifyNickname("Annoyed")).thenReturn(auditableRestAction);
        when(auditableRestAction.reason(anyString())).thenReturn(auditableRestAction);

        when(member.modifyNickname("Tired")).thenReturn(auditableRestAction);
        when(secondJoke.modifyNickname("Nin")).thenReturn(auditableRestAction);

        dadbot.dadJoke("tired", member);
        dadbot.dadJoke("annoyed", secondJoke);
        verify(member).modifyNickname(anyString());
        verify(secondJoke).modifyNickname(anyString());
        assertThat(dadbot.getOriginalNicknames()).isNotEmpty();
        assertThat(dadbot.getOriginalNicknames()).hasSize(1);
        assertThat(dadbot.getOriginalNicknames().get("1-2")).isNotEmpty();
        assertThat(dadbot.getOriginalNicknames().get("1-2")).hasSize(2);
        assertThat(dadbot.getOriginalNicknames().get("1-2")).contains("Nin", "Tired");

        verify(auditableRestAction, times(2)).queue(lambdaCaptor.capture());
        verify(member).modifyNickname(anyString());
        verify(secondJoke).modifyNickname(anyString());
        
        var voidConsumers = lambdaCaptor.getAllValues();

        voidConsumers.getFirst().accept(voidmock);
        assertThat(dadbot.getOriginalNicknames()).isNotEmpty();
        assertThat(dadbot.getOriginalNicknames().get("1-2")).hasSize(1);
        assertThat(dadbot.getOriginalNicknames().get("1-2")).contains("Nin");
        voidConsumers.getLast().accept(voidmock);

        assertThat(dadbot.getOriginalNicknames()).isEmpty();
    }
}
