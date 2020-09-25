package dev.nincodedo.ninbot.components.ac.turnips;

import dev.nincodedo.ninbot.components.ac.turnips.generator.TurnipPriceGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Log4j2
@Component
public class TurnipPricesManager {

    private final TurnipPricesRepository turnipPricesRepository;
    private final TurnipPriceGenerator turnipPriceGenerator;
    private final Random random;

    public TurnipPricesManager(TurnipPricesRepository turnipPricesRepository,
            TurnipPriceGenerator turnipPriceGenerator) {
        this.turnipPricesRepository = turnipPricesRepository;
        this.random = new Random();
        this.turnipPriceGenerator = turnipPriceGenerator;
    }

    void generateNewWeek() {
        TurnipPrices turnipPrices = new TurnipPrices();
        turnipPrices.setSeed(random.nextLong());
        turnipPrices.setCreated(LocalDateTime.now());
        turnipPricesRepository.save(turnipPrices);
        log.trace("Seed set for next turnip price week");
    }

    List<Integer> getTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        return turnipPriceGenerator.getTurnipPricesList(turnipPattern, seed);
    }

    int getSundayTurnipPrices(long seed) {
        return turnipPriceGenerator.getSundayTurnipPrice(seed);
    }

    public List<TurnipPrices> findAll() {
        return turnipPricesRepository.findAll();
    }

    public TurnipPrices findNewest() {
        return turnipPricesRepository.findAllByOrderByCreatedDesc().get(0);
    }

    public TurnipPattern getTurnipPattern(long seed) {
        return turnipPriceGenerator.getTurnipPattern(seed);
    }
}
