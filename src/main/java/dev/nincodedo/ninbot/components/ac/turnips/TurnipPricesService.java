package dev.nincodedo.ninbot.components.ac.turnips;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class TurnipPricesService {
    private TurnipPricesRepository turnipPricesRepository;

    public TurnipPricesService(TurnipPricesRepository turnipPricesRepository) {
        this.turnipPricesRepository = turnipPricesRepository;
    }

    public TurnipPattern getPreviousPattern(long seed) {
        final java.time.LocalDateTime lastWeek = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        final java.time.LocalDateTime twoWeeksAgo = LocalDateTime.now().minus(14, ChronoUnit.DAYS);
        final java.util.Optional<dev.nincodedo.ninbot.components.ac.turnips.TurnipPrices> optionalPrice =
                turnipPricesRepository
                        .findFirstByCreatedBetween(lastWeek, twoWeeksAgo);
        //if there's a previous week for the turnips, then return that pattern, otherwise generate one based on the seed
        long serverSeed = optionalPrice.map(TurnipPrices::getSeed).orElse(seed);
        return TurnipPattern.getRandomTurnipPattern(serverSeed);
    }
}
