package dev.nincodedo.ninbot.components.reaction;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

class ReactionResponse {
    protected String response;
    protected String target;
    protected List<String> responses;
    protected ReactionMatchType reactionMatchType;

    public ReactionResponse() {
    }

    public ReactionResponse(ReactionResponse reactionResponse) {
        this.response = reactionResponse.response;
        this.target = reactionResponse.target;
        this.responses = reactionResponse.responses;
        this.reactionMatchType = reactionResponse.reactionMatchType;
    }

    void react(Message message, MessageChannel channel) {
        //NO-OP
    }

    boolean canRespond(MessageReceivedEvent event) {
        return switch (reactionMatchType) {
            case EXACT -> getTarget().equalsIgnoreCase(event.getMessage().getContentStripped());
            case CONTAINS -> event.getMessage().getContentStripped().toLowerCase().contains(getTarget().toLowerCase());
            case REGEX -> event.getMessage().getContentStripped().matches(getTarget());
        };
    }


    public String getResponse() {
        return this.response;
    }

    public void setResponse(final String response) {
        this.response = response;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public List<String> getResponses() {
        return this.responses;
    }

    public void setResponses(final List<String> responses) {
        this.responses = responses;
    }

    public ReactionMatchType getReactionMatchType() {
        return this.reactionMatchType;
    }

    public void setReactionMatchType(final ReactionMatchType reactionMatchType) {
        this.reactionMatchType = reactionMatchType;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof ReactionResponse)) return false;
        final ReactionResponse other = (ReactionResponse) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$response = this.getResponse();
        final java.lang.Object other$response = other.getResponse();
        if (this$response == null ? other$response != null : !this$response.equals(other$response)) return false;
        final java.lang.Object this$target = this.getTarget();
        final java.lang.Object other$target = other.getTarget();
        if (this$target == null ? other$target != null : !this$target.equals(other$target)) return false;
        final java.lang.Object this$responses = this.getResponses();
        final java.lang.Object other$responses = other.getResponses();
        if (this$responses == null ? other$responses != null : !this$responses.equals(other$responses)) return false;
        final java.lang.Object this$reactionMatchType = this.getReactionMatchType();
        final java.lang.Object other$reactionMatchType = other.getReactionMatchType();
        return this$reactionMatchType == null ?
                other$reactionMatchType == null : this$reactionMatchType.equals(other$reactionMatchType);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof ReactionResponse;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $response = this.getResponse();
        result = result * PRIME + ($response == null ? 43 : $response.hashCode());
        final java.lang.Object $target = this.getTarget();
        result = result * PRIME + ($target == null ? 43 : $target.hashCode());
        final java.lang.Object $responses = this.getResponses();
        result = result * PRIME + ($responses == null ? 43 : $responses.hashCode());
        final java.lang.Object $reactionMatchType = this.getReactionMatchType();
        result = result * PRIME + ($reactionMatchType == null ? 43 : $reactionMatchType.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "ReactionResponse(response=" + this.getResponse() + ", target=" + this.getTarget() + ", responses="
                + this.getResponses() + ", reactionMatchType=" + this.getReactionMatchType() + ")";
    }
}
