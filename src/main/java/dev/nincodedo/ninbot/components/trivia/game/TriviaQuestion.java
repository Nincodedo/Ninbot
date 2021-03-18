package dev.nincodedo.ninbot.components.trivia.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

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


    public String getCategory() {
        return this.category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(final String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    @JsonProperty("correct_answer")

    public void setCorrectAnswer(final String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return this.incorrectAnswers;
    }

    @JsonProperty("incorrect_answers")

    public void setIncorrectAnswers(final List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TriviaQuestion)) return false;
        final TriviaQuestion other = (TriviaQuestion) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$category = this.getCategory();
        final java.lang.Object other$category = other.getCategory();
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) return false;
        final java.lang.Object this$type = this.getType();
        final java.lang.Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final java.lang.Object this$difficulty = this.getDifficulty();
        final java.lang.Object other$difficulty = other.getDifficulty();
        if (this$difficulty == null ? other$difficulty != null : !this$difficulty.equals(other$difficulty))
            return false;
        final java.lang.Object this$question = this.getQuestion();
        final java.lang.Object other$question = other.getQuestion();
        if (this$question == null ? other$question != null : !this$question.equals(other$question)) return false;
        final java.lang.Object this$correctAnswer = this.getCorrectAnswer();
        final java.lang.Object other$correctAnswer = other.getCorrectAnswer();
        if (this$correctAnswer == null ? other$correctAnswer != null : !this$correctAnswer.equals(other$correctAnswer))
            return false;
        final java.lang.Object this$incorrectAnswers = this.getIncorrectAnswers();
        final java.lang.Object other$incorrectAnswers = other.getIncorrectAnswers();
        return this$incorrectAnswers == null ?
                other$incorrectAnswers == null : this$incorrectAnswers.equals(other$incorrectAnswers);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof TriviaQuestion;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $category = this.getCategory();
        result = result * PRIME + ($category == null ? 43 : $category.hashCode());
        final java.lang.Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final java.lang.Object $difficulty = this.getDifficulty();
        result = result * PRIME + ($difficulty == null ? 43 : $difficulty.hashCode());
        final java.lang.Object $question = this.getQuestion();
        result = result * PRIME + ($question == null ? 43 : $question.hashCode());
        final java.lang.Object $correctAnswer = this.getCorrectAnswer();
        result = result * PRIME + ($correctAnswer == null ? 43 : $correctAnswer.hashCode());
        final java.lang.Object $incorrectAnswers = this.getIncorrectAnswers();
        result = result * PRIME + ($incorrectAnswers == null ? 43 : $incorrectAnswers.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "TriviaQuestion(category=" + this.getCategory() + ", type=" + this.getType() + ", difficulty="
                + this.getDifficulty() + ", question=" + this.getQuestion() + ", correctAnswer="
                + this.getCorrectAnswer() + ", incorrectAnswers=" + this.getIncorrectAnswers() + ")";
    }
}
