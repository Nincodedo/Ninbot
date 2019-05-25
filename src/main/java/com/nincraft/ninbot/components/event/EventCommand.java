package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

import static java.awt.Color.BLUE;

@Component
public class EventCommand extends AbstractCommand {

    private EventRepository eventRepository;
    private EventScheduler eventScheduler;
    private EventParser eventParser;
    private ConfigService configService;

    public EventCommand(EventRepository eventRepository, EventScheduler eventScheduler, ConfigService configService) {
        length = 3;
        name = "events";
        checkExactLength = false;
        this.eventRepository = eventRepository;
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
        val eventList = new ArrayList<Event>();
        eventRepository.findAll().forEach(eventList::add);
        eventList.sort(Comparator.comparing(Event::getStartTime));
        if (eventList.isEmpty()) {
            return new MessageBuilder().append(resourceBundle.getString("command.event.list.noeventsfound")).build();
        }
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle(resourceBundle.getString("command.event.list.title"));
        messageBuilder.setColor(BLUE);
        for (val event : eventList) {
            event.setResourceBundle(resourceBundle);
            messageBuilder.addField(event.getName(), event.toString(), true);
        }
        messageBuilder.setFooter(resourceBundle.getString("command.event.list.footer") + serverTimezone, null);
        return messageBuilder.build();
    }

    private String getServerTimeZone(String serverId) {
        return configService.getSingleValueByName(serverId, ConfigConstants.SERVER_TIMEZONE)
                .orElse(ConfigConstants.DEFAULT_TIMEZONE);
    }
}
