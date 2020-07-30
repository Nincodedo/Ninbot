package com.nincraft.ninbot.components.trivia.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

@Data
public class TriviaQuestion {
    private String category;
    private String type;
    private String difficulty;
    private String question;
    @JsonProperty("correct_answer")
    private String correctAnswer;
    @JsonProperty("incorrect_answers")
    private List<String> incorrectAnswers;

    public TriviaQuestion(JsonNode triviaResults) {
        this.category = triviaResults.get("category").asText();
        this.type = triviaResults.get("type").asText();
        this.difficulty = triviaResults.get("difficulty").asText();
        this.question = triviaResults.get("question").asText();
        this.correctAnswer = triviaResults.get("correct_answer").asText();
        this.incorrectAnswers = triviaResults.findValuesAsText("incorrect_answers");
    }


    public Message build() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(question);
        embedBuilder.setAuthor(category);
        return new MessageBuilder(embedBuilder).build();
    }

    public void unescapeFields() {
        category = StringEscapeUtils.unescapeHtml3(category).trim();
        type = StringEscapeUtils.unescapeHtml3(type).trim();
        difficulty = StringEscapeUtils.unescapeHtml3(difficulty).trim();
        question = StringEscapeUtils.unescapeHtml3(question).trim();
        correctAnswer = StringEscapeUtils.unescapeHtml3(correctAnswer).trim();
    }
}
