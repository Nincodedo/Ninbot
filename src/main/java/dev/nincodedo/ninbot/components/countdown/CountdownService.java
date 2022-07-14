package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.common.Scheduler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountdownService implements Scheduler<Countdown, CountdownRepository> {

    private CountdownRepository countdownRepository;

    public CountdownService(CountdownRepository countdownRepository) {
        this.countdownRepository = countdownRepository;
    }

    @Override
    public List<Countdown> findAllOpenItems() {
        return countdownRepository.findAll();
    }

    @Override
    public CountdownRepository getRepository() {
        return countdownRepository;
    }

    public void delete(Countdown countdown) {
        countdownRepository.delete(countdown);
    }
}
