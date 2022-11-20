package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.ninbot.NinbotRunner;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class NinbotInfoCommandImplTest {

    @Mock
    ShardManager shardManager;

    @Mock
    NinbotBotInfo ninbotBotInfo;

    @InjectMocks
    NinbotInfoCommandImpl ninbotInfoCommandImpl;

    @Test
    void getPatronsList() {
        Guild guild = Mockito.mock(Guild.class);
        Member member = Mockito.mock(Member.class);
        User user = Mockito.mock(User.class);
        Member member2 = Mockito.mock(Member.class);
        User user2 = Mockito.mock(User.class);
        Member bot = Mockito.mock(Member.class);
        User userBot = Mockito.mock(User.class);
        Member owner = Mockito.mock(Member.class);


        List<Member> memberList = List.of(member, member2, bot, owner);

        when(shardManager.getGuildById(ninbotBotInfo.getSupporterGuildId())).thenReturn(guild);
        when(guild.getMembersWithRoles(Collections.emptyList())).thenReturn(memberList);
        when(owner.isOwner()).thenReturn(true);
        when(member.getUser()).thenReturn(user);
        when(member2.getUser()).thenReturn(user2);
        when(bot.getUser()).thenReturn(userBot);
        when(userBot.isBot()).thenReturn(true);
        when(user.getName()).thenReturn("User 1");
        when(user2.getName()).thenReturn("User 2");

        var actualString = ninbotInfoCommandImpl.getPatronsList(shardManager);

        assertThat(actualString).isNotEmpty()
                .contains(", ")
                .contains("User 1")
                .contains("User 2")
                .doesNotContain("Bot");
    }
}
