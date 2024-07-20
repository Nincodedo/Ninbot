package dev.nincodedo.ninbot.components.haiku;


import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HaikuMessageParserTest {

    HaikuMessageParser haikuMessageParser = new HaikuMessageParser();

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
        assertThat(haikuMessageParser.isHaikuable(unhaikuable)).isNotPresent();
    }

    @ParameterizedTest
    @MethodSource("haikuables")
    void messageHaikuable(String haikuable) {
        assertThat(haikuMessageParser.isHaikuable(haikuable)).isPresent();
    }
}
