package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.nincord.message.EphemeralMessageExecutor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserCommandTestIT {

    UserRepository userRepository;
    UserCommand userCommand;

    @Autowired
    public UserCommandTestIT(UserCommand userCommand, UserRepository userRepository) {
        this.userCommand = userCommand;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void before() {
        userRepository.deleteAll();
    }

    @Test
    void addBirthdayUpdateAnnouncement() {
        assertThat(userRepository.findAll()).isEmpty();
        SlashCommandInteractionEvent addBirthdayEvent = Mockito.mock(SlashCommandInteractionEvent.class);
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        when(addBirthdayEvent.getSubcommandName()).thenReturn("birthday");
        when(addBirthdayEvent.getMember()).thenReturn(member);
        when(addBirthdayEvent.getGuild()).thenReturn(guild);
        when(member.getId()).thenReturn("1");
        when(guild.getId()).thenReturn("1");

        var me = (EphemeralMessageExecutor) userCommand.execute(addBirthdayEvent);

        assertThat(me.getEphemeralMessageResponses()).isNotEmpty();
        var userOptional = userRepository.getFirstByUserId("1");
        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getUserId()).isEqualTo("1");
        assertThat(userOptional.get().getAnnounceBirthday()).isFalse();

        SlashCommandInteractionEvent updateAnnouncement = Mockito.mock(SlashCommandInteractionEvent.class);
        when(updateAnnouncement.getSubcommandName()).thenReturn("announcement");
        when(updateAnnouncement.getMember()).thenReturn(member);
        when(updateAnnouncement.getGuild()).thenReturn(guild);

        var me2 = (EphemeralMessageExecutor) userCommand.execute(updateAnnouncement);

        assertThat(me2.getEphemeralMessageResponses()).isNotEmpty();
        var userOptional2 = userRepository.getFirstByUserId("1");
        assertThat(userOptional2).isPresent();
        assertThat(userOptional2.get().getUserId()).isEqualTo("1");
        assertThat(userOptional2.get().getAnnounceBirthday()).isTrue();
    }
}
