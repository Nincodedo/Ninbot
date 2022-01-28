package dev.nincodedo.ninbot.components.countdown;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CountdownRepository extends CrudRepository<Countdown, Long> {
    List<Countdown> findByServerId(String serverId);

    Optional<Countdown> findByCreatorIdAndName(String creatorId, String name);
}
