package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.common.release.ReleaseType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DegreesOfNinbotTest {

    @Test
    void publicReleaseAllowed() {
        Guild guild = Mockito.mock(Guild.class);
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        JDA jda = Mockito.mock(JDA.class);
        ShardManager shardManager = Mockito.mock(ShardManager.class);

        when(guild.getOwner()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("1");
        when(guild.getJDA()).thenReturn(jda);
        when(jda.getShardManager()).thenReturn(shardManager);
        when(shardManager.getGuildById("109466144993210368")).thenReturn(guild);
        when(guild.getMember(any())).thenReturn(null);

        var actual = DegreesOfNinbot.releaseAllowed(ReleaseType.PUBLIC, guild);

        assertThat(actual).isTrue();
    }

    @Test
    void alphaReleaseAllowed() {
        Guild guild = Mockito.mock(Guild.class);
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        JDA jda = Mockito.mock(JDA.class);
        ShardManager shardManager = Mockito.mock(ShardManager.class);

        when(guild.getOwner()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("109466144993210368");
        when(guild.getJDA()).thenReturn(jda);
        when(jda.getShardManager()).thenReturn(shardManager);
        when(jda.getGuildById("109466144993210368")).thenReturn(guild);
        when(guild.getMember(any())).thenReturn(null);

        var actual = DegreesOfNinbot.releaseAllowed(ReleaseType.ALPHA, guild);

        assertThat(actual).isTrue();
    }

    @Test
    void alphaReleaseNotAllowed() {
        Guild guild = Mockito.mock(Guild.class);
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        JDA jda = Mockito.mock(JDA.class);
        ShardManager shardManager = Mockito.mock(ShardManager.class);

        when(guild.getOwner()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("1");
        when(guild.getJDA()).thenReturn(jda);
        when(jda.getShardManager()).thenReturn(shardManager);
        when(shardManager.getGuildById("109466144993210368")).thenReturn(guild);
        when(guild.getMember(any())).thenReturn(null);

        var actual = DegreesOfNinbot.releaseAllowed(ReleaseType.ALPHA, guild);

        assertThat(actual).isFalse();
    }

    @Test
    void betaReleaseAllowed() {
        Guild guild = Mockito.mock(Guild.class);
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        JDA jda = Mockito.mock(JDA.class);
        ShardManager shardManager = Mockito.mock(ShardManager.class);

        when(guild.getOwner()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("1");
        when(guild.getJDA()).thenReturn(jda);
        when(jda.getShardManager()).thenReturn(shardManager);
        when(shardManager.getGuildById("109466144993210368")).thenReturn(guild);
        when(guild.getMember(user)).thenReturn(member);

        var actual = DegreesOfNinbot.releaseAllowed(ReleaseType.BETA, guild);

        assertThat(actual).isTrue();
    }

    @Test
    void betaReleaseNotAllowed() {
        Guild guild = Mockito.mock(Guild.class);
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        JDA jda = Mockito.mock(JDA.class);
        ShardManager shardManager = Mockito.mock(ShardManager.class);

        when(guild.getOwner()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("1");
        when(guild.getJDA()).thenReturn(jda);
        when(jda.getShardManager()).thenReturn(shardManager);
        when(shardManager.getGuildById("109466144993210368")).thenReturn(guild);
        when(guild.getMember(user)).thenReturn(null);

        var actual = DegreesOfNinbot.releaseAllowed(ReleaseType.BETA, guild);

        assertThat(actual).isFalse();
    }
}