package com.nincraft.ninbot.components.reaction;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class ReactionListener extends ListenerAdapter {

    private static List<String> badCharacters = Arrays.asList(" ", "!", ":");

    private Map<String, ReactionResponse> responseMap = new HashMap<>();

    public ReactionListener() {
        loadResponseMap();
    }

    private void loadResponseMap() {
        try {
            List<String> lines = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("responses.txt"), Charset.defaultCharset());
            for (val line : lines) {
                responseMap.put(line.split("\\|")[0].toLowerCase(), generateResponse(line.split("\\|")[1]));
            }
        } catch (IOException e) {
            log.error("Failed to read responses file", e);
        }
    }

    private ReactionResponse generateResponse(String response) {
        if (isCanEmoji(response)) {
            return new EmojiReactionResponse(response);
        } else {
            return new StringReactionResponse(response);
        }
    }

    private boolean isCanEmoji(String response) {
        if (containsBadCharacters(response)) {
            return false;
        }
        for (char c : response.toCharArray()) {
            String check = StringUtils.replaceOnce(response, Character.toString(c), "");
            if (check.contains(Character.toString(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsBadCharacters(String response) {
        return badCharacters.stream().anyMatch(response::contains);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            respond(event);
        }
    }

    private void respond(MessageReceivedEvent event) {
        val response = responseMap.get(event.getMessage().getContentStripped().toLowerCase());

        if (response != null) {
            response.react(event.getMessage(), event.getChannel());
        }
    }
}
