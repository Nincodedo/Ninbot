package dev.nincodedo.ninbot.components.trivia;

import dev.nincodedo.ninbot.components.trivia.game.TriviaQuestion;

import javax.persistence.*;
import java.util.Timer;

@Entity
public class TriviaInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String channelId;
    @Column(nullable = false)
    private String serverId;
    private int categoryId;
    @Column(nullable = false)
    private String apiToken;
    private String question;
    private String answer;
    @Transient
    private TriviaQuestion triviaQuestion;
    @Transient
    private Timer triviaTimer;

    public TriviaInstance(String serverId, String channelId, int categoryId) {
        this.serverId = serverId;
        this.channelId = channelId;
        this.categoryId = categoryId;
    }

    public TriviaInstance() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getChannelId() {
        return this.channelId;
    }

    public void setChannelId(final String channelId) {
        this.channelId = channelId;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(final int categoryId) {
        this.categoryId = categoryId;
    }

    public String getApiToken() {
        return this.apiToken;
    }

    public void setApiToken(final String apiToken) {
        this.apiToken = apiToken;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(final String answer) {
        this.answer = answer;
    }

    public TriviaQuestion getTriviaQuestion() {
        return this.triviaQuestion;
    }

    public void setTriviaQuestion(final TriviaQuestion triviaQuestion) {
        this.triviaQuestion = triviaQuestion;
    }

    public Timer getTriviaTimer() {
        return this.triviaTimer;
    }

    public void setTriviaTimer(final Timer triviaTimer) {
        this.triviaTimer = triviaTimer;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TriviaInstance)) return false;
        final TriviaInstance other = (TriviaInstance) o;
        if (!other.canEqual(this)) return false;
        if (this.getCategoryId() != other.getCategoryId()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$channelId = this.getChannelId();
        final java.lang.Object other$channelId = other.getChannelId();
        if (this$channelId == null ? other$channelId != null : !this$channelId.equals(other$channelId)) return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        if (this$serverId == null ? other$serverId != null : !this$serverId.equals(other$serverId)) return false;
        final java.lang.Object this$apiToken = this.getApiToken();
        final java.lang.Object other$apiToken = other.getApiToken();
        if (this$apiToken == null ? other$apiToken != null : !this$apiToken.equals(other$apiToken)) return false;
        final java.lang.Object this$question = this.getQuestion();
        final java.lang.Object other$question = other.getQuestion();
        if (this$question == null ? other$question != null : !this$question.equals(other$question)) return false;
        final java.lang.Object this$answer = this.getAnswer();
        final java.lang.Object other$answer = other.getAnswer();
        if (this$answer == null ? other$answer != null : !this$answer.equals(other$answer)) return false;
        final java.lang.Object this$triviaQuestion = this.getTriviaQuestion();
        final java.lang.Object other$triviaQuestion = other.getTriviaQuestion();
        if (this$triviaQuestion == null ?
                other$triviaQuestion != null : !this$triviaQuestion.equals(other$triviaQuestion)) return false;
        final java.lang.Object this$triviaTimer = this.getTriviaTimer();
        final java.lang.Object other$triviaTimer = other.getTriviaTimer();
        return this$triviaTimer == null ? other$triviaTimer == null : this$triviaTimer.equals(other$triviaTimer);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof TriviaInstance;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getCategoryId();
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $channelId = this.getChannelId();
        result = result * PRIME + ($channelId == null ? 43 : $channelId.hashCode());
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        final java.lang.Object $apiToken = this.getApiToken();
        result = result * PRIME + ($apiToken == null ? 43 : $apiToken.hashCode());
        final java.lang.Object $question = this.getQuestion();
        result = result * PRIME + ($question == null ? 43 : $question.hashCode());
        final java.lang.Object $answer = this.getAnswer();
        result = result * PRIME + ($answer == null ? 43 : $answer.hashCode());
        final java.lang.Object $triviaQuestion = this.getTriviaQuestion();
        result = result * PRIME + ($triviaQuestion == null ? 43 : $triviaQuestion.hashCode());
        final java.lang.Object $triviaTimer = this.getTriviaTimer();
        result = result * PRIME + ($triviaTimer == null ? 43 : $triviaTimer.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "TriviaInstance(id=" + this.getId() + ", channelId=" + this.getChannelId() + ", serverId="
                + this.getServerId() + ", categoryId=" + this.getCategoryId() + ", apiToken=" + this.getApiToken()
                + ", question=" + this.getQuestion() + ", answer=" + this.getAnswer() + ", triviaQuestion="
                + this.getTriviaQuestion() + ", triviaTimer=" + this.getTriviaTimer() + ")";
    }
}
