package com.nincraft.ninbot.components.trivia;

import org.springframework.data.repository.CrudRepository;

public interface TriviaInstanceRepository extends CrudRepository<TriviaInstance, Long> {
    boolean existsByChannelId(String channelId);

    void deleteByChannelId(String channelId);
}
