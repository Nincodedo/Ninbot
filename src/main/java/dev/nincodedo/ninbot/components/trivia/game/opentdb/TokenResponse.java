package dev.nincodedo.ninbot.components.trivia.game.opentdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {
    private String token;


    public TokenResponse() {
    }


    public String getToken() {
        return this.token;
    }


    public void setToken(final String token) {
        this.token = token;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TokenResponse)) return false;
        final TokenResponse other = (TokenResponse) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$token = this.getToken();
        final java.lang.Object other$token = other.getToken();
        return this$token == null ? other$token == null : this$token.equals(other$token);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof TokenResponse;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $token = this.getToken();
        result = result * PRIME + ($token == null ? 43 : $token.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "TokenResponse(token=" + this.getToken() + ")";
    }
}
