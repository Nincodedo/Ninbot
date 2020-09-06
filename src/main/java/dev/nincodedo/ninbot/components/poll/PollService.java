package dev.nincodedo.ninbot.components.poll;

import org.springframework.stereotype.Service;

@Service
public class PollService {

    private PollRepository pollRepository;

    public PollService(PollRepository pollRepository){
        this.pollRepository = pollRepository;
    }
}
