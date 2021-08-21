package dev.nincodedo.ninbot.components.ac.turnips;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.components.ac.turnips.generator.TurnipPriceGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class TurnipPriceGeneratorTest {

    @Mock
    TurnipPricesService turnipPricesService;

    @InjectMocks
    TurnipPriceGenerator turnipPriceGenerator;

    private static Stream<TestData> turnipPatternTestData() {
        return Stream.of(
                new TestData(5L, TurnipPattern.SMALL_SPIKE),
                new TestData(10L, TurnipPattern.RANDOM),
                new TestData(15L, TurnipPattern.BIG_SPIKE),
                new TestData(20L, TurnipPattern.DECREASING)
        );
    }

    @Test
    void getSundayTurnipPrice() {
        var expectedPrice = 97;

        var actualPrice = turnipPriceGenerator.getSundayTurnipPrice(5L);

        assertThat(actualPrice).isEqualTo(expectedPrice);
    }

    @Test
    void randomPrices() {
        var expectedPrices = Arrays.asList(117, 134, 128, 109, 73, 67, 59, 123, 65, 59, 103, 120);

        var actualPrices = turnipPriceGenerator.getTurnipPricesList(TurnipPattern.RANDOM, 5L);

        assertThat(actualPrices).hasSize(12).isEqualTo(expectedPrices);
    }

    @Test
    void smallSpikePrices() {
        var expectedPrices = Arrays.asList(48, 44, 39, 35, 31, 26, 23, 110, 123, 141, 155, 136);

        var actualPrices = turnipPriceGenerator.getTurnipPricesList(TurnipPattern.SMALL_SPIKE, 5L);

        assertThat(actualPrices).hasSize(12).isEqualTo(expectedPrices);
    }

    @Test
    void decreasingPrices() {
        var expectedPrices = Arrays.asList(84, 81, 77, 74, 70, 65, 62, 58, 54, 50, 45, 42);

        var actualPrices = turnipPriceGenerator.getTurnipPricesList(TurnipPattern.DECREASING, 5L);

        assertThat(actualPrices).hasSize(12).isEqualTo(expectedPrices);
    }

    @Test
    void bigSpikePrices() {
        var expectedPrices = Arrays.asList(84, 80, 75, 70, 67, 105, 185, 374, 179, 103, 55, 40);

        var actualPrices = turnipPriceGenerator.getTurnipPricesList(TurnipPattern.BIG_SPIKE, 5L);

        assertThat(actualPrices).hasSize(12).isEqualTo(expectedPrices);
    }

    @ParameterizedTest
    @MethodSource("turnipPatternTestData")
    void getTurnipPattern(TestData testData) {
        when(turnipPricesService.getPreviousPattern(testData.input)).thenReturn(TurnipPattern.RANDOM);

        var actualPattern = turnipPriceGenerator.getTurnipPattern(testData.input);

        Assertions.assertThat(actualPattern).isEqualTo(testData.expected);
    }

    record TestData(long input, TurnipPattern expected) {
    }
}