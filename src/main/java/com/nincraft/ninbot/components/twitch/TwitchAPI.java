package com.nincraft.ninbot.components.twitch;

import com.github.twitch4j.helix.TwitchHelix;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TwitchAPI {

    private TwitchHelix twitchHelix;

    public TwitchAPI(TwitchHelix twitchHelix) {
        this.twitchHelix = twitchHelix;
    }

    String getBoxArtUrl(String gameName) {
        val gameResults = twitchHelix.getGames(null, Arrays.asList(gameName), null).execute();
        if (gameResults.getGames().isEmpty()) {
            return null;
        } else {
            String boxartUrl = gameResults.getGames().get(0).getBoxArtUrl();
            return boxartUrl.replace("-{width}x{height}", "");
        }
    }
}
