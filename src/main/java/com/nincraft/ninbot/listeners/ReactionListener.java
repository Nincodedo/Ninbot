package com.nincraft.ninbot.listeners;

import com.nincraft.ninbot.util.MessageSenderHelper;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class ReactionListener extends ListenerAdapter {

    private Map<String, String> responseMap = new HashMap<>();

    public ReactionListener() {
        loadResponseMap();
    }

    private void loadResponseMap() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/responses.txt"));
            for (val line : lines) {
                responseMap.put(line.split("\\|")[0], line.split("\\|")[1]);
            }
        } catch (IOException e) {
            log.error("Failed to read responses file", e);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            respond(event);
        }
    }

    private void respond(MessageReceivedEvent event) {
        String response = responseMap.get(event.getMessage().getContent().toLowerCase());
        if (StringUtils.isNotBlank(response)) {
            MessageSenderHelper.sendMessage(event.getChannel(), response);
        }
    }
}
