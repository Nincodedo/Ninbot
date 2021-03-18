package dev.nincodedo.ninbot.components.trivia;

import javax.persistence.*;
import java.util.Comparator;

@Entity
public class TriviaScore implements Comparable<TriviaScore> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private int score;

    public TriviaScore() {
        //no-op
    }

    TriviaScore(String userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(TriviaScore otherScore) {
        return Comparator.comparing(TriviaScore::getScore)
                .thenComparing(TriviaScore::getUserId)
                .compare(this, otherScore);
    }


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TriviaScore)) return false;
        final TriviaScore other = (TriviaScore) o;
        if (!other.canEqual(this)) return false;
        if (this.getScore() != other.getScore()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        return this$userId == null ? other$userId == null : this$userId.equals(other$userId);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof TriviaScore;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getScore();
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "TriviaScore(id=" + this.getId() + ", userId=" + this.getUserId() + ", score=" + this.getScore() + ")";
    }
}
