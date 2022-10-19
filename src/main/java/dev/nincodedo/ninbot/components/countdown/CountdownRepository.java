package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CountdownRepository extends BaseRepository<Countdown> {

    default List<Countdown> findByServerId(String serverId) {
        return findByServerIdAndDeleted(serverId, false);
    }

    default List<Countdown> findCountdownByAudit_CreatedBy(String creatorId) {
        return findCountdownByAudit_CreatedByAndDeleted(creatorId, false);
    }

    default Optional<Countdown> findByAudit_CreatedByAndName(String creatorId, String name) {
        return findByAudit_CreatedByAndNameAndDeleted(creatorId, name, false);
    }

    List<Countdown> findByServerIdAndDeleted(String serverId, Boolean isDeleted);

    List<Countdown> findCountdownByAudit_CreatedByAndDeleted(String creatorId, Boolean isDeleted);

    Optional<Countdown> findByAudit_CreatedByAndNameAndDeleted(String creatorId, String name, Boolean isDeleted);
}
