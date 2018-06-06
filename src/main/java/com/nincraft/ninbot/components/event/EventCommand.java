package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.command.AbstractCommand;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.nincraft.ninbot.util.MessageUtils.reactSuccessfulResponse;
import static com.nincraft.ninbot.util.MessageUtils.sendMessage;
import static java.awt.Color.BLUE;
import static java.time.OffsetDateTime.now;
import static java.time.OffsetDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@Component
public class EventCommand extends AbstractCommand {

    private EventService eventService;
    private EventScheduler eventScheduler;

    public EventCommand(EventService eventService, EventScheduler eventScheduler) {
        length = 3;
        name = "events";
        description = "list/plan events, use @Ninbot events help for more details";
        checkExactLength = false;
        this.eventService = eventService;
        this.eventScheduler = eventScheduler;
    }

    @Override
    public void executeCommand(MessageReceivedEvent messageReceivedEvent) {
        val content = messageReceivedEvent.getMessage().getContentStripped().toLowerCase();
        val channel = messageReceivedEvent.getChannel();
        if (isCommandLengthCorrect(content)) {
            val action = getSubcommand(content);
            switch (action) {
                case "list":
                    listEvents(channel);
                    break;
                case "plan":
                    planEvent(messageReceivedEvent.getMessage(), messageReceivedEvent.getAuthor(), messageReceivedEvent.getJDA());
                    break;
                case "help":
                    displayEventHelp(channel);
                    break;
                default:
                    sendMessage(channel, "Not a valid events sub command");
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
                "Event times are in GMT -6, formatted \"2017-01-31T12:00:00-06:00\" for January 31st 2017 at noon";
        sendMessage(channel, helpMessage);
    }

    private void planEvent(Message message, User author, JDA jda) {
        Event event = new Event();
        Map<String, String> eventMap = parsePlanMessage(message.getContentStripped());
        event.setAuthorName(author.getName())
                .setGameName(eventMap.get("gameName"))
                .setName(eventMap.get("name"))
                .setStartTime(parse(eventMap.get("startTime"), ISO_OFFSET_DATE_TIME))
                .setServerId(message.getGuild().getId());
        eventScheduler.addEvent(event, jda);
        reactSuccessfulResponse(message);
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
        val events = eventService.getAllEvents();
        if (events.isEmpty()) {
            sendMessage(channel, "No events scheduled");
            return;
        }
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.append("Current scheduled events");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(BLUE);
        for (val event : events) {
            embedBuilder.addField(event.getName(), event.toString(), true);
        }
        embedBuilder.setFooter("All times are in GMT " + now().getOffset(), null);
        messageBuilder.setEmbed(embedBuilder.build());
        sendMessage(channel, messageBuilder.build());
    }
}
