package com.nincraft.ninbot.components.ac.turnips;

import com.nincraft.ninbot.NinbotTest;
import lombok.val;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnipPricesManagerTest extends NinbotTest {

    @InjectMocks
    TurnipPricesManager turnipPricesManager;

    private long seed = 5;

    @Test
    public void getTurnipPricesListRandom() {
        TurnipPattern turnipPattern = TurnipPattern.RANDOM;
        val priceList = turnipPricesManager.getTurnipPricesList(turnipPattern, seed);
        assertThat(priceList).containsExactly(187, 192, 124, 124, 56, 55, 54, 91, 172, 71, 181, 53);
        for (int price : priceList) {
            assertThat(price).isBetween(50, 200);
        }
    }

    @Test
    public void getTurnipPricesListSmallSpike() {
        TurnipPattern turnipPattern = TurnipPattern.SMALL_SPIKE;
        val priceList = turnipPricesManager.getTurnipPricesList(turnipPattern, seed);
        assertThat(priceList).containsExactly(94, 90, 84, 78, 75, 73, 231, 255, 255, 270, 135, 49);
        assertThat(priceList.get(0)).isBetween(50, 99);

    }

    @Test
    public void getTurnipPricesListBigSpike() {
        TurnipPattern turnipPattern = TurnipPattern.BIG_SPIKE;
        val priceList = turnipPricesManager.getTurnipPricesList(turnipPattern, seed);
        assertThat(priceList).containsExactly(94, 90, 84, 78, 75, 73, 67, 222, 376, 607, 233, 218);
        assertThat(priceList.get(0)).isBetween(50, 99);
    }

    @Test
    public void getTurnipPricesListDecreasing() {
        TurnipPattern turnipPattern = TurnipPattern.DECREASING;
        val priceList = turnipPricesManager.getTurnipPricesList(turnipPattern, seed);
        assertThat(priceList).containsExactly(94, 90, 84, 78, 75, 73, 67, 64, 60, 57, 54, 49);
        assertThat(priceList.get(0)).isBetween(50, 99);
    }

    @Test
    public void getSundayTurnipPrices() {
        int sundayPrice = turnipPricesManager.getSundayTurnipPrices(seed);
        assertThat(sundayPrice).isBetween(90, 110);
    }
}