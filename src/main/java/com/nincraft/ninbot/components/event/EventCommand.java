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

import java.time.LocalDate;

import static java.awt.Color.BLUE;
import static java.time.OffsetDateTime.now;

@Component
public class EventCommand extends AbstractCommand {

    private EventService eventService;
    private EventScheduler eventScheduler;
    private EventParser eventParser;

    public EventCommand(EventService eventService, EventScheduler eventScheduler) {
        length = 3;
        name = "events";
        description = "list/plan events, use events help for more details";
        checkExactLength = false;
        this.eventService = eventService;
        this.eventScheduler = eventScheduler;
        this.eventParser = new EventParser();
        helpText = "Use \"@Ninbot events plan\" to add an event to the schedule\n" +
                "Parameters: @Ninbot events plan \"Event Name\" StartTime GameName\n" +
                "Note: event name must be in quotes if it is longer than one word\n" +
                "Event times are in GMT -6, formatted \"" + LocalDate.now().getYear() + "-01-31T12:00:00-06:00\" "
                + "for January 31st " + LocalDate.now().getYear() + " at noon";
    }

    @Override
    public void executeCommand(MessageReceivedEvent messageReceivedEvent) {
        val content = messageReceivedEvent.getMessage().getContentStripped().toLowerCase();
        val channel = messageReceivedEvent.getChannel();
        if (isCommandLengthCorrect(content)) {
            switch (getSubcommand(content)) {
                case "list":
                    listEvents(channel);
                    break;
                case "plan":
                    planEvent(messageReceivedEvent.getMessage(), messageReceivedEvent.getAuthor(), messageReceivedEvent.getJDA());
                    break;
                default:
                    messageUtils.reactUnknownResponse(messageReceivedEvent.getMessage());
                    break;
            }
        } else {
            messageUtils.reactUnknownResponse(messageReceivedEvent.getMessage());
        }
    }

    private void planEvent(Message message, User author, JDA jda) {
        Event event = eventParser.parsePlanMessage(message, author.getName());
        eventScheduler.addEvent(event, jda);
        messageUtils.reactSuccessfulResponse(message);
    }

    private void listEvents(MessageChannel channel) {
        val events = eventService.getAllEvents();
        if (events.isEmpty()) {
            messageUtils.sendMessage(channel, "No events scheduled");
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
        messageUtils.sendMessage(channel, messageBuilder.build());
    }
}
