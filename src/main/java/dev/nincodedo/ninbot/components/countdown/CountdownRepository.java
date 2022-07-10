package dev.nincodedo.ninbot.components.countdown;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CountdownRepository extends CrudRepository<Countdown, Long> {

    @NotNull List<Countdown> findAll();

    List<Countdown> findByServerId(String serverId);

    List<Countdown> findCountdownByCreatedBy(String creatorId);

    Optional<Countdown> findByCreatedByAndName(String creatorId, String name);
}
