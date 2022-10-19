package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends BaseRepository<Poll> {
    default List<Poll> findAllByPollOpen(boolean isPollOpen) {
        return findAllByPollOpenAndDeleted(isPollOpen, false);
    }

    default Optional<Poll> findByMessageIdAndPollOpen(String messageId, boolean isPollOpen) {
        return findByMessageIdAndPollOpenAndDeleted(messageId, isPollOpen, false);
    }

    List<Poll> findAllByPollOpenAndDeleted(boolean isPollOpen, Boolean isDeleted);

    Optional<Poll> findByMessageIdAndPollOpenAndDeleted(String messageId, boolean isPollOpen, Boolean isDeleted);

}
