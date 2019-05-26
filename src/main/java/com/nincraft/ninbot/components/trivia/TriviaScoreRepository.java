package com.nincraft.ninbot.components.trivia;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TriviaScoreRepository extends CrudRepository<TriviaScore, Long> {
    Optional<TriviaScore> getFirstByUserId(String userId);
}
