package dev.nincodedo.ninbot.components.ac.turnips;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TurnipPricesRepository extends CrudRepository<TurnipPrices, Long> {
    List<TurnipPrices> findAll();

    List<TurnipPrices> findAllByOrderByCreatedAsc();

    List<TurnipPrices> findAllByOrderByCreatedDesc();

    Optional<TurnipPrices> findFirstByCreatedBetween(LocalDateTime begin, LocalDateTime end);
}
