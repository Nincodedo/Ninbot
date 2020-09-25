package dev.nincodedo.ninbot.components.ac.turnips.generator;

import dev.nincodedo.ninbot.components.ac.turnips.TurnipPattern;
import dev.nincodedo.ninbot.components.ac.turnips.TurnipPricesService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static dev.nincodedo.ninbot.components.ac.turnips.TurnipPattern.*;

@Component
public class TurnipPriceGenerator {

    private Random random;
    private TurnipPricesService turnipPricesService;

    public TurnipPriceGenerator(TurnipPricesService turnipPricesService) {
        this.turnipPricesService = turnipPricesService;
    }

    public TurnipPattern getTurnipPattern(long seed) {
        random = new Random(seed);
        int chance = random.nextInt(100);
        TurnipPattern previousPattern = turnipPricesService.getPreviousPattern(seed);
        TurnipPattern nextPattern;
        List<Integer> chanceList = switch (previousPattern) {
            case RANDOM -> Arrays.asList(20, 50, 65);
            case BIG_SPIKE -> Arrays.asList(50, 55, 75);
            case DECREASING -> Arrays.asList(25, 70, 75);
            case SMALL_SPIKE -> Arrays.asList(45, 70, 85);
        };
        if (chance < chanceList.get(0)) {
            nextPattern = RANDOM;
        } else if (chance < chanceList.get(1)) {
            nextPattern = BIG_SPIKE;
        } else if (chance < chanceList.get(2)) {
            nextPattern = DECREASING;
        } else {
            nextPattern = TurnipPattern.SMALL_SPIKE;
        }
        return nextPattern;
    }

    public List<Integer> getTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        int basePrice = getSundayTurnipPrice(seed);
        random = new Random(seed);
        PatternPriceGenerator priceGenerator = switch (turnipPattern) {
            case RANDOM -> new RandomPriceGenerator();
            case BIG_SPIKE -> new BigSpikePriceGenerator();
            case DECREASING -> new DecreasingPriceGenerator();
            case SMALL_SPIKE -> new SmallSpikePriceGenerator();
        };
        return priceGenerator.generatePrices(basePrice, random);
    }

    public int getSundayTurnipPrice(long seed) {
        return new Random(seed).nextInt(20) + 90;
    }
}
