package dev.nincodedo.ninbot.components.poll;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PollRepository extends CrudRepository<Poll, Long> {
    List<Poll> findAllByPollOpen(boolean isPollOpen);
}
