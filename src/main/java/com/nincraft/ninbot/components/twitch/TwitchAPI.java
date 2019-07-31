package com.nincraft.ninbot.components.twitch;

import com.github.twitch4j.helix.TwitchHelix;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Log4j2
public class TwitchAPI {

    private TwitchHelix twitchHelix;

    public TwitchAPI(TwitchHelix twitchHelix) {
        this.twitchHelix = twitchHelix;
    }

    String getBoxArtUrl(String gameName) {
        try {
            log.trace("Attempting to get boxart for {}", gameName);
            val gameResults = twitchHelix.getGames(null, null, Arrays.asList(gameName)).execute();
            log.trace("Found {} results from Twitch", gameResults.getGames().size());
            if (gameResults.getGames().isEmpty()) {
                log.trace("Found nothing, returning null");
                return null;
            } else {
                String boxartUrl = gameResults.getGames().get(0).getBoxArtUrl().replace("-{width}x{height}", "");
                log.trace("Found boxart, returning {}", boxartUrl);
                return boxartUrl;
            }
        } catch (Exception e) {
            log.error("Failed to get boxart from Twitch API", e);
            return null;
        }
    }
}
