package com.nincraft.ninbot.components.event;

import lombok.val;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;
import java.util.Map;

import static java.time.OffsetDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

class EventParser {

    Event parsePlanMessage(Message message, String author) {
        Event event = new Event();
        Map<String, String> eventMap = parseMessage(message.getContentStripped());
        event.setAuthorName(author)
                .setGameName(eventMap.get("gameName"))
                .setName(eventMap.get("name"))
                .setStartTime(parse(eventMap.get("startTime"), ISO_OFFSET_DATE_TIME))
                .setServerId(message.getGuild().getId());
        return event;
    }

    private Map<String, String> parseMessage(String content) {
        Map<String, String> eventMap = new HashMap<>();
        val messageList = content.split(" ");
        int counter = 3;
        StringBuilder name = new StringBuilder();
        if (content.contains("\"")) {
            String currentWord = messageList[counter];
            name.append(currentWord).append(" ");
            while (!currentWord.endsWith("\"") && counter < 22) {
                counter++;
                name.append(messageList[counter]).append(" ");
                currentWord = messageList[counter];
            }
        } else {
            name.append(messageList[counter++]);
        }
        eventMap.put("name", name.toString().replace("\"", "").substring(0, name.length() - 3));
        eventMap.put("startTime", messageList[++counter]);
        eventMap.put("gameName", messageList[++counter]);
        return eventMap;
    }
}
