package dev.nincodedo.ninbot.components.haiku;

import dev.nincodedo.nincord.message.MessageContextInteractionEventMessageExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class HaikuAnalyzeCommandTest {

    HaikuMessageParser haikuMessageParser = new HaikuMessageParser();

    HaikuAnalyzeCommand haikuAnalyzeCommand = new HaikuAnalyzeCommand(haikuMessageParser);

    static List<String> nonhaikuables() {
        return List.of("too short",
                "Because Amazon continues to be a curse on my work life.",
                "that is a diaminopropyltetramethylenediamine", "welcome welcome welcome place place place place "
                        + "place place place place place place place", "place place place place place welcome welcome"
                        + " welcome welcome place place place place", "place place place place place place place "
                        + "place place place place place welcome welcome", "", "wow numbers like this 51261");
    }

    static List<String> haikuables() {
        return List.of("the the the the the the the the the the the the the the the the the", "the the the the the "
                        + "the the the the the the the the the the the 9",
                "Because Amazon continues to be a curse on my whole work life.", "Thank you Nintendo for Nintendo"
                        + " 64 it so a haiku");
    }

    @ParameterizedTest
    @MethodSource("nonhaikuables")
    void nonHaikuTest(String words) {
        var event = Mockito.mock(MessageContextInteractionEvent.class);
        var messageExecutor = new MessageContextInteractionEventMessageExecutor(event);
        var message = Mockito.mock(Message.class);
        var jda = Mockito.mock(JDA.class);
        var user = Mockito.mock(User.class);

        when(event.getTarget()).thenReturn(message);
        when(message.getAuthor()).thenReturn(user);
        when(event.getJDA()).thenReturn(jda);
        when(message.getContentStripped()).thenReturn(words);
        when(message.getContentRaw()).thenReturn(words);

        var result = (MessageContextInteractionEventMessageExecutor) haikuAnalyzeCommand.execute(event,
                messageExecutor);

        assertThat(result.getEphemeralMessageResponses()).isNotEmpty();
        var embeds = result.getEphemeralMessageResponses().getFirst().getEmbeds();
        log.debug("Results: {}", embeds.getFirst()
                .getFields()
                .stream()
                .map(MessageEmbed.Field::getValue)
                .collect(Collectors.joining()));
        assertThat(embeds).isNotEmpty();
        assertThat(embeds.getFirst().getFields()).isNotEmpty();
        assertThat(embeds.getFirst().getFields().getLast().getValue()).contains("Not");
    }

    @ParameterizedTest
    @MethodSource("haikuables")
    void haikuTest(String words) {
        var event = Mockito.mock(MessageContextInteractionEvent.class);
        var messageExecutor = new MessageContextInteractionEventMessageExecutor(event);
        var message = Mockito.mock(Message.class);
        var jda = Mockito.mock(JDA.class);
        var user = Mockito.mock(User.class);

        when(event.getTarget()).thenReturn(message);
        when(message.getAuthor()).thenReturn(user);
        when(event.getJDA()).thenReturn(jda);
        when(message.getContentStripped()).thenReturn(words);
        when(message.getContentRaw()).thenReturn(words);

        var result = (MessageContextInteractionEventMessageExecutor) haikuAnalyzeCommand.execute(event,
                messageExecutor);

        assertThat(result.getEphemeralMessageResponses()).isNotEmpty();
        var embeds = result.getEphemeralMessageResponses().getFirst().getEmbeds();
        log.debug("Results: {}", embeds.getFirst()
                .getFields()
                .stream()
                .map(MessageEmbed.Field::getValue)
                .collect(Collectors.joining()));
        assertThat(embeds).isNotEmpty();
        assertThat(embeds.getFirst().getFields()).isNotEmpty();
        assertThat(embeds.getFirst().getFields().getLast().getValue()).doesNotContain("Not");
    }
}
