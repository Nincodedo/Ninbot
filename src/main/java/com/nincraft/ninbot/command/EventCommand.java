package com.nincraft.ninbot.command;

import com.nincraft.ninbot.Ninbot;
import com.nincraft.ninbot.dao.IEventDao;
import com.nincraft.ninbot.entity.Event;
import com.nincraft.ninbot.scheduler.EventScheduler;
import com.nincraft.ninbot.util.MessageSenderHelper;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class EventCommand extends AbstractCommand {

    private IEventDao eventDao;
    private EventScheduler eventScheduler;

    public EventCommand() {
        length = 3;
        name = "events";
        description = "list/plan events, use @Ninbot events help for more details";
        eventDao = Ninbot.getEventDao();
        eventScheduler = Ninbot.getEventScheduler();
    }

    @Override
    public void executeCommand(MessageReceivedEvent messageReceivedEvent) {
        val content = messageReceivedEvent.getMessage().getContent().toLowerCase();
        val channel = messageReceivedEvent.getChannel();
        if (isCommandLengthCorrect(content)) {
            val action = getSubcommand(content);
            switch (action) {
                case "list":
                    listEvents(channel);
                    break;
                case "plan":
                    planEvent(messageReceivedEvent.getMessage(), messageReceivedEvent.getAuthor(), channel);
                    break;
                case "help":
                    displayEventHelp(channel);
                    break;
                default:
                    MessageSenderHelper.sendMessage(channel, "Not a valid events sub command");
                    break;
            }
        } else {
            wrongCommandLengthMessage(channel);
        }
    }

    private void displayEventHelp(MessageChannel channel) {
        String helpMessage = "Use \"@Ninbot events plan\" to add an event to the schedule\n" +
                "Parameters: @Ninbot events plan \"Event Name\" StartTime GameName\n" +
                "Note: event name must be in quotes if it is longer than one word\n" +
                "Event times are in GMT -6, formatted \"2017-01-31T12:00:00\" for January 31st 2017 at noon";
        MessageSenderHelper.sendMessage(channel, helpMessage);
    }

    private void planEvent(Message message, User author, MessageChannel channel) {
        Event event = new Event();
        Map<String, String> eventMap = parsePlanMessage(message.getContent());
        event.setAuthorName(author.getName())
                .setGameName(eventMap.get("gameName"))
                .setName(eventMap.get("name"))
                .setStartTime(LocalDateTime.parse(eventMap.get("startTime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME).atOffset(ZoneOffset.of("-06:00")).toLocalDateTime());
        eventScheduler.addEvent(event);
        MessageSenderHelper.sendMessage(channel, "Added %s to schedule", event.getName());
    }

    private Map<String, String> parsePlanMessage(String content) {
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

    private void listEvents(MessageChannel channel) {
        MessageSenderHelper.sendMessage(channel, eventDao.getAllEvents().toString());
    }

    @Override
    boolean isCommandLengthCorrect(String content) {
        return content.split(" ").length >= length;
    }
}
