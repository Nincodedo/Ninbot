package dev.nincodedo.ninbot.components.countdown;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CountdownRepository extends CrudRepository<Countdown, Long> {

    @NotNull List<Countdown> findAll();

    List<Countdown> findByServerId(String serverId);

    List<Countdown> findCountdownByAudit_CreatedBy(String creatorId);

    Optional<Countdown> findByAudit_CreatedByAndName(String creatorId, String name);
}
