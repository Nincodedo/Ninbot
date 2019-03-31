package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
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
    private ConfigService configService;

    public EventCommand(EventService eventService, EventScheduler eventScheduler, ConfigService configService) {
        length = 3;
        name = "events";
        description = "list/plan events, use events help for more details";
        checkExactLength = false;
        this.eventService = eventService;
        this.eventScheduler = eventScheduler;
        this.configService = configService;
        this.eventParser = new EventParser();
        helpText = "Use \"@Ninbot events plan\" to add an event to the schedule\n" +
                "Parameters: @Ninbot events plan \"Event Name\" StartTime GameName\n" +
                "Note: event name must be in quotes if it is longer than one word\n" +
                "Event times are in GMT -6, formatted \"" + LocalDate.now().getYear() + "-01-31T12:00:00-06:00\" "
                + "for January 31st " + LocalDate.now().getYear() + " at noon";
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent messageReceivedEvent) {
        CommandResult commandResult = new CommandResult(messageReceivedEvent);
        val content = messageReceivedEvent.getMessage().getContentStripped().toLowerCase();
        if (isCommandLengthCorrect(content)) {
            val serverTimezone = getServerTimeZone(messageReceivedEvent.getGuild().getId());
            switch (getSubcommand(content)) {
                case "list":
                    commandResult.addChannelAction(listEvents(serverTimezone));
                    break;
                case "plan":
                    planEvent(messageReceivedEvent, serverTimezone);
                    commandResult.addSuccessfulReaction();
                    break;
                default:
                    commandResult.addUnknownReaction();
                    break;
            }
        } else {
            commandResult.addUnknownReaction();
        }
        return commandResult;
    }

    private void planEvent(MessageReceivedEvent messageReceivedEvent, String serverTimezone) {
        Event event = eventParser.parsePlanMessage(messageReceivedEvent.getMessage(), messageReceivedEvent.getAuthor().getName(), serverTimezone);
        eventScheduler.addEvent(event, messageReceivedEvent.getJDA());
    }

    private Message listEvents(String serverTimezone) {
        val events = eventService.getAllEvents();
        if (events.isEmpty()) {
            return new MessageBuilder().append("No events scheduled").build();
        }
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle("Current scheduled events");
        messageBuilder.setColor(BLUE);
        for (val event : events) {
            messageBuilder.addField(event.getName(), event.toString(), true);
        }
        messageBuilder.setFooter("All times are in GMT " + serverTimezone, null);
        return messageBuilder.build();
    }

    private String getServerTimeZone(String serverId) {
        return configService.getSingleValueByName(serverId, ConfigConstants.SERVER_TIMEZONE)
                .orElse(ConfigConstants.DEFAULT_TIMEZONE);
    }
}
