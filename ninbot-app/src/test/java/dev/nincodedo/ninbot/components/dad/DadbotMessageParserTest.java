package dev.nincodedo.ninbot.components.dad;

import dev.nincodedo.nincord.LocaleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class DadbotMessageParserTest {

    DadbotMessageParser dadbotMessageParser = new DadbotMessageParser();

    public static List<String> dadReplies() {
        return List.of("I'm happy", "im glad", "i'm tired", "hi im sad", "I'm ready for anything", "I'm a really long"
                        + " message with another sentence in it. This should still reply correctly.",
                "This is also a really long message with another sentence in it, but the dad part doesn't happen "
                        + "until later. I'm sure it will still work.", "I'm this, and this.", "I'm Dr. Mario and "
                        + "this is my favorite Discord server.",
                """
                        Long ago in a distant land, I Aku unleashed an unspeakable evil.
                        
                        It was a multiline string.
                        
                        I'm sure this is fine.
                        """, "I'm using more punctuation than required!!!", "I'm saying I'm multiple times and that's"
                        + " normal I'm sure.", "the beginning of this sentence doesn't have anything good in it but "
                        + "I'm sure in the end it will.");
    }

    public static List<String> noDadReplies() {
        return List.of("I'm", "I'm ", "Just words that don't have that word", "I have never claimed to be smart.",
                "This claim is false.");
    }

    @ParameterizedTest
    @MethodSource("dadReplies")
    void dadReply(String message) {
        var actual = dadbotMessageParser.dadReply(message, message);
        assertThat(actual).isPresent();
        log.debug(String.format(LocaleService.getResourceBundleOrDefault(null)
                .getString("listener.dad.joke"), actual.get()));
    }

    @ParameterizedTest
    @MethodSource("noDadReplies")
    void noDadReply(String message) {
        var actual = dadbotMessageParser.dadReply(message, message);
        assertThat(actual).isEmpty();
    }
}
