package com.nincraft.ninbot.components.trivia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

@Data
public class TriviaQuestion {
    public String category;
    public String type;
    public String difficulty;
    public String question;
    @JsonProperty("correct_answer")
    public String correctAnswer;
    @JsonProperty("incorrect_answers")
    public List<String> incorrectAnswers;


    public Message build() {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(question);
        embedBuilder.setAuthor(category);
        messageBuilder.setEmbed(embedBuilder.build());
        return messageBuilder.build();
    }

    void unescapeFields() {
        category = StringEscapeUtils.unescapeHtml3(category).trim();
        type = StringEscapeUtils.unescapeHtml3(type).trim();
        difficulty = StringEscapeUtils.unescapeHtml3(difficulty).trim();
        question = StringEscapeUtils.unescapeHtml3(question).trim();
        correctAnswer = StringEscapeUtils.unescapeHtml3(correctAnswer).trim();
    }
}
