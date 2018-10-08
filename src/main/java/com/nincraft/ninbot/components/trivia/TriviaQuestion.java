package com.nincraft.ninbot.components.trivia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.Collections;
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
        StringBuilder builder = new StringBuilder();
        List<String> choices = new ArrayList<>();
        choices.add(correctAnswer);
        choices.addAll(incorrectAnswers);
        Collections.shuffle(choices);
        for (val choice : choices) {
            builder.append(choice).append("\n");
        }
        embedBuilder.addField("Possible Answers", builder.toString(), true);
        messageBuilder.setEmbed(embedBuilder.build());
        return messageBuilder.build();
    }
}
