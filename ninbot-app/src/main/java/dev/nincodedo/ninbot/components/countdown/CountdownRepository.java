package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CountdownRepository extends BaseRepository<Countdown> {

    List<Countdown> findByServerId(String serverId);

    List<Countdown> findCountdownByAudit_CreatedBy(String creatorId);

    Optional<Countdown> findByAudit_CreatedByAndName(String creatorId, String name);
}
