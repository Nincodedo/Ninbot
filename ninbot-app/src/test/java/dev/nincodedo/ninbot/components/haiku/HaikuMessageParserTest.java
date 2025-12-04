package dev.nincodedo.ninbot.components.haiku;


import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HaikuMessageParserTest {

    HaikuMessageParser haikuMessageParser = new HaikuMessageParser();

    @ParameterizedTest
    @ArgumentsSource(NonhaikuableArgumentsProvider.class)
    void messagesUnhaikuable(String unhaikuable) {
        assertThat(haikuMessageParser.isHaikuable(unhaikuable)).isNotPresent();
    }

    @ParameterizedTest
    @ArgumentsSource(HaikuableArgumentsProvider.class)
    void messageHaikuable(String haikuable) {
        assertThat(haikuMessageParser.isHaikuable(haikuable)).isPresent();
    }
}
