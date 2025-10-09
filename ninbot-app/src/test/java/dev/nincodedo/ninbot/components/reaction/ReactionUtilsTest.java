package dev.nincodedo.ninbot.components.reaction;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ReactionUtilsTest {

    public static Stream<String> emojitizableWords() {
        return Stream.of("big", "grand", "thanks", "uncopyrightable", "dermatoglyphics", "how dare");
    }

    public static Stream<String> notEmojitizableWords() {
        return Stream.of("biig", "graand", "thaanks", "how dare you", "what!");
    }

    @ParameterizedTest
    @MethodSource("emojitizableWords")
    void words(String emojiText) {
        var canEmoji = ReactionUtils.isCanEmoji(emojiText);
        assertThat(canEmoji).isTrue();
        var emojis = String.join("", new EmojiReactionResponse(emojiText).getEmojiList());
        assertThat(emojis).isNotBlank();
        log.info(emojis);
    }

    @ParameterizedTest
    @MethodSource("notEmojitizableWords")
    void noWords(String emojiText) {
        var canEmoji = ReactionUtils.isCanEmoji(emojiText);
        assertThat(canEmoji).isFalse();
    }
}
