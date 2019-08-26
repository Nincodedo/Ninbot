package com.nincraft.ninbot.components.ac.turnips;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.TestUtils;
import com.nincraft.ninbot.components.ac.VillagerManager;
import com.nincraft.ninbot.components.common.Emojis;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TurnipCommandTest extends NinbotTest {

    @Mock
    VillagerManager villagerManager;

    @InjectMocks
    TurnipCommand turnipCommand;

    @Test
    public void executeHelpCommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips");
        val commandResults = turnipCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnPrivateMessageEmbededName(commandResults)).contains("Turnips Command Help");
    }

    @Test
    public void executeJoinCommand() {
        User user = Mockito.mock(User.class);
        Guild guild = Mockito.mock(Guild.class);
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips join");
        when(messageEvent.getAuthor()).thenReturn(user);
        when(user.getId()).thenReturn("1");
        when(messageEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        val commandResults = turnipCommand.executeCommand(messageEvent);
        assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CHECK_MARK);
    }

    @Test
    public void executeBuyCommand() {
        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getContentStripped()).thenReturn("@Ninbot turnips buy 10");
        val commandResults = turnipCommand.executeCommand(messageEvent);
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CHECK_MARK);
        } else {
            assertThat(TestUtils.returnEmoji(commandResults)).contains(Emojis.CROSS_X);
        }
    }
}