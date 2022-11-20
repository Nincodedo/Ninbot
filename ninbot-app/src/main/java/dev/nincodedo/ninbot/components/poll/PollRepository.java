package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends BaseRepository<Poll> {

    Optional<Poll> findByMessageIdAndPollOpen(String messageId, boolean isPollOpen);

    List<Poll> findAllByIdIn(List<Long> ids);

    List<Poll> findAllByPollOpen(Boolean isOpen);
}
