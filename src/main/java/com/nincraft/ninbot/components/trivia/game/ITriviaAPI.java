package com.nincraft.ninbot.components.trivia.game;

import com.nincraft.ninbot.components.trivia.TriviaInstance;

import java.util.Map;

public interface ITriviaAPI {
    TriviaQuestion nextTriviaQuestion(TriviaInstance triviaInstance);

    Map<Integer, String> getTriviaCategories();
}
