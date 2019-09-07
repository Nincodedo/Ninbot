package com.nincraft.ninbot.components.ac.turnips;

import com.nincraft.ninbot.NinbotTest;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class TurnipPricesManagerTest extends NinbotTest {

    @InjectMocks
    TurnipPricesManager turnipPricesManager;

    private long seed = 5;
    private double expectedAverageProfitPerTurnip = 160;
    private double expectedAverageProfitableWeeks = 0.7;

    @Test
    public void simulateManyWeeks() {
        int weeks = 10000000;
        int lowest = Integer.MAX_VALUE;
        int highest = 0;
        List<Integer> profits = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            int highestWeek = 0;
            Random random = new Random();
            long seed = random.nextLong();
            int sundayPrice = turnipPricesManager.getSundayTurnipPrices(seed);
            random.setSeed(seed);
            TurnipPattern turnipPattern = TurnipPattern.getRandomTurnipPattern(random.nextLong());
            log.trace("Week #{}: Seed: {}, Pattern: {}", i + 1, seed, turnipPattern);
            val priceList = turnipPricesManager.getTurnipPricesList(turnipPattern, random.nextLong());
            log.trace("Price list: {}", priceList);
            for (int price : priceList) {
                if (price > highest) {
                    highest = price;
                }
                if (price < lowest) {
                    lowest = price;
                }
                if (price > highestWeek) {
                    highestWeek = price;
                }
                assertThat(price).isGreaterThan(0);
            }
            profits.add(highestWeek - sundayPrice);
        }
        double averageProfitableWeeks = profits.stream()
                .mapToInt(i -> i)
                .filter(value -> value > 0)
                .count() / (double) weeks;
        assertThat(averageProfitableWeeks).isCloseTo(expectedAverageProfitableWeeks, Percentage.withPercentage(10));
        double averageProfit = profits.stream().mapToInt(value -> value).average().getAsDouble();
        assertThat(averageProfit).isCloseTo(expectedAverageProfitPerTurnip, Percentage.withPercentage(10));
    }

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
        assertThat(priceList).containsExactly(94, 92, 90, 146, 153, 164, 203, 146, 65, 62, 57, 52);
        assertThat(priceList.get(0)).isBetween(50, 99);

    }

    @Test
    public void getTurnipPricesListBigSpike() {
        TurnipPattern turnipPattern = TurnipPattern.BIG_SPIKE;
        val priceList = turnipPricesManager.getTurnipPricesList(turnipPattern, seed);
        assertThat(priceList).containsExactly(94, 92, 90, 86, 83, 78, 75, 222, 376, 607, 233, 218);
        assertThat(priceList.get(0)).isBetween(50, 99);
    }

    @Test
    public void getTurnipPricesListDecreasing() {
        TurnipPattern turnipPattern = TurnipPattern.DECREASING;
        val priceList = turnipPricesManager.getTurnipPricesList(turnipPattern, seed);
        assertThat(priceList).containsExactly(94, 92, 90, 86, 83, 78, 75, 70, 67, 64, 60, 55);
        assertThat(priceList.get(0)).isBetween(50, 99);
    }

    @Test
    public void getSundayTurnipPrices() {
        int sundayPrice = turnipPricesManager.getSundayTurnipPrices(seed);
        assertThat(sundayPrice).isBetween(90, 110);
    }
}