package dev.nincodedo.ninbot.components.trivia.game.opentdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TriviaCategoryResponse {
    @JsonProperty(value = "trivia_categories")
    private List<TriviaCategory> triviaCategoryList;
}
