package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.nincord.Scheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PollService implements Scheduler<Poll, PollRepository> {
    private PollRepository pollRepository;

    public PollService(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Override
    public List<Poll> findAllOpenItems() {
        return pollRepository.findAllByPollOpen(true);
    }

    @Override
    public void save(Poll poll) {
        pollRepository.save(poll);
    }

    @Override
    public PollRepository getRepository() {
        return pollRepository;
    }

    public Optional<Poll> findByMessageIdAndPollOpen(String pollMessageId, boolean pollOpen) {
        return pollRepository.findByMessageIdAndPollOpen(pollMessageId, pollOpen);
    }

    public List<Poll> findAllClosedPolls() {
        return pollRepository.findAllByPollOpen(false);
    }
}
