package dev.nincodedo.ninbot.components.trivia.game.opentdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TriviaCategoryResponse {
    @JsonProperty("trivia_categories")
    private List<TriviaCategory> triviaCategoryList;


    public TriviaCategoryResponse() {
    }


    public List<TriviaCategory> getTriviaCategoryList() {
        return this.triviaCategoryList;
    }

    @JsonProperty("trivia_categories")

    public void setTriviaCategoryList(final List<TriviaCategory> triviaCategoryList) {
        this.triviaCategoryList = triviaCategoryList;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TriviaCategoryResponse)) return false;
        final TriviaCategoryResponse other = (TriviaCategoryResponse) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$triviaCategoryList = this.getTriviaCategoryList();
        final java.lang.Object other$triviaCategoryList = other.getTriviaCategoryList();
        return this$triviaCategoryList == null ?
                other$triviaCategoryList == null : this$triviaCategoryList.equals(other$triviaCategoryList);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof TriviaCategoryResponse;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $triviaCategoryList = this.getTriviaCategoryList();
        result = result * PRIME + ($triviaCategoryList == null ? 43 : $triviaCategoryList.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "TriviaCategoryResponse(triviaCategoryList=" + this.getTriviaCategoryList() + ")";
    }
}
