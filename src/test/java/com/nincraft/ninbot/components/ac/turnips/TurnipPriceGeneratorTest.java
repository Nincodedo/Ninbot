package com.nincraft.ninbot.components.ac.turnips;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.components.ac.turnips.generator.TurnipPriceGenerator;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class TurnipPriceGeneratorTest extends NinbotTest {

    @Mock
    TurnipPricesRepository turnipPricesRepository;

    @InjectMocks
    TurnipPriceGenerator turnipPriceGenerator;

    @Test
    void getSundayTurnipPrice(){
        val expectedPrice = 97;
        val actualPrice = turnipPriceGenerator.getSundayTurnipPrice(5L);
        assertThat(actualPrice).isEqualTo(expectedPrice);
    }

    @Test
    void getRandomTurnipPricesList() {
        val expectedPrices = Arrays.asList(117, 134, 128, 109, 73, 67, 59, 123, 65, 59, 103, 120);
        val actualPrices = turnipPriceGenerator.getTurnipPricesList(TurnipPattern.RANDOM, 5L);
        assertThat(actualPrices).hasSize(12);
        assertThat(actualPrices).isEqualTo(expectedPrices);
    }

    @Test
    void smallSpikePrices() {
        val expectedPrices = Arrays.asList(48, 44, 39, 35, 31, 26, 23, 110, 123, 141, 155, 136);
        val actualPrices = turnipPriceGenerator.getTurnipPricesList(TurnipPattern.SMALL_SPIKE, 5L);
        assertThat(actualPrices).hasSize(12);
        assertThat(actualPrices).isEqualTo(expectedPrices);
    }

    @Test
    void decreasingPrices() {
        val expectedPrices = Arrays.asList(84, 81, 77, 74, 70, 65, 62, 58, 54, 50, 45, 42);
        val actualPrices = turnipPriceGenerator.getTurnipPricesList(TurnipPattern.DECREASING, 5L);
        assertThat(actualPrices).hasSize(12);
        assertThat(actualPrices).isEqualTo(expectedPrices);
    }

    @Test
    void bigSpikePrices() {
        val expectedPrices = Arrays.asList(84, 80, 75, 70, 67, 105, 185, 374, 179, 103, 55, 40);
        val actualPrices = turnipPriceGenerator.getTurnipPricesList(TurnipPattern.BIG_SPIKE, 5L);
        assertThat(actualPrices).hasSize(12);
        assertThat(actualPrices).isEqualTo(expectedPrices);
    }

    @Test
    void getTurnipPattern() {
    }
}