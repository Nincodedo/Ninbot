package com.nincraft.ninbot.components.trivia.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
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


    public Message build() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(question);
        embedBuilder.setAuthor(category);
        return new MessageBuilder(embedBuilder).build();
    }

    void unescapeFields() {
        category = StringEscapeUtils.unescapeHtml3(category).trim();
        type = StringEscapeUtils.unescapeHtml3(type).trim();
        difficulty = StringEscapeUtils.unescapeHtml3(difficulty).trim();
        question = StringEscapeUtils.unescapeHtml3(question).trim();
        correctAnswer = StringEscapeUtils.unescapeHtml3(correctAnswer).trim();
    }
}
