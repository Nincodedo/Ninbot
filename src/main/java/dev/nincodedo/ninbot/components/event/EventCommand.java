package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;

import static java.awt.Color.BLUE;

@Component
public class EventCommand extends AbstractCommand {

    private EventRepository eventRepository;
    private EventScheduler eventScheduler;
    private EventParser eventParser;

    public EventCommand(EventRepository eventRepository, EventScheduler eventScheduler) {
        length = 3;
        name = "events";
        checkExactLength = false;
        this.eventRepository = eventRepository;
        this.eventScheduler = eventScheduler;
        this.eventParser = new EventParser();
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent messageReceivedEvent) {
        MessageAction messageAction = new MessageAction(messageReceivedEvent);
        val content = messageReceivedEvent.getMessage().getContentStripped().toLowerCase();
        if (isCommandLengthCorrect(content)) {
            val serverTimezone = getServerTimeZone(messageReceivedEvent.getGuild().getId());
            switch (getSubcommand(content)) {
                case "list" -> messageAction.addChannelAction(listEvents(serverTimezone));
                case "plan" -> {
                    planEvent(messageReceivedEvent, serverTimezone);
                    messageAction.addSuccessfulReaction();
                }
                default -> messageAction.addUnknownReaction();
            }
        } else {
            messageAction.addUnknownReaction();
        }
        return messageAction;
    }

    private void planEvent(MessageReceivedEvent messageReceivedEvent, String serverTimezone) {
        Event event = eventParser.parsePlanMessage(messageReceivedEvent.getMessage(), messageReceivedEvent.getAuthor()
                .getName(), serverTimezone);
        eventScheduler.addEvent(event, messageReceivedEvent.getJDA().getShardManager());
    }

    private Message listEvents(String serverTimezone) {
        val eventList = new ArrayList<Event>();
        eventRepository.findAll().forEach(eventList::add);
        eventList.sort(Comparator.comparing(Event::getStartTime));
        if (eventList.isEmpty()) {
            return new MessageBuilder().append(resourceBundle.getString("command.event.list.noeventsfound")).build();
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(resourceBundle.getString("command.event.list.title"));
        embedBuilder.setColor(BLUE);
        for (val event : eventList) {
            event.setResourceBundle(resourceBundle);
            embedBuilder.addField(event.getName(), event.toString(), true);
        }
        embedBuilder.setFooter(resourceBundle.getString("command.event.list.footer") + serverTimezone, null);
        return new MessageBuilder(embedBuilder).build();
    }

    private String getServerTimeZone(String serverId) {
        return configService.getSingleValueByName(serverId, ConfigConstants.SERVER_TIMEZONE)
                .orElse(ConfigConstants.DEFAULT_TIMEZONE);
    }
}
