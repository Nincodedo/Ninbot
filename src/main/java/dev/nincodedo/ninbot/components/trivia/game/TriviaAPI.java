package dev.nincodedo.ninbot.components.trivia.game;

import dev.nincodedo.ninbot.components.trivia.TriviaInstance;

import java.util.Map;

public interface TriviaAPI {
    TriviaQuestion nextTriviaQuestion(TriviaInstance triviaInstance);

    Map<Integer, String> getTriviaCategories();
}
