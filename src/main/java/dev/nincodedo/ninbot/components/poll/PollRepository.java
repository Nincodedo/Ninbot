package dev.nincodedo.ninbot.components.poll;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends CrudRepository<Poll, Long> {
    List<Poll> findAllByPollOpen(boolean isPollOpen);
    Optional<Poll> findByMessageIdAndPollOpen(String messageId, boolean isPollOpen);
    Optional<Poll> findById(Long id);
}
