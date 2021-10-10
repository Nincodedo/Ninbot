package dev.nincodedo.ninbot.components.poll;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PollService {
    private PollRepository pollRepository;

    public PollService(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    public void save(Poll poll) {
        pollRepository.save(poll);
    }

    public Optional<Poll> findByMessageIdAndPollOpen(String pollMessageId, boolean pollOpen) {
        return pollRepository.findByMessageIdAndPollOpen(pollMessageId, pollOpen);
    }

    public List<Poll> findAllOpenPolls() {
        return pollRepository.findAllByPollOpen(true);
    }

    public List<Poll> findAllClosedPolls() {
        return pollRepository.findAllByPollOpen(false);
    }
}
