package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.components.command.SlashCommand;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.awt.Color.BLUE;

@Component
public class EventCommand implements SlashCommand {

    private EventRepository eventRepository;
    private EventScheduler eventScheduler;
    private EventParser eventParser;
    private ConfigService configService;

    public EventCommand(EventRepository eventRepository, EventScheduler eventScheduler, ConfigService configService) {
        this.eventRepository = eventRepository;
        this.eventScheduler = eventScheduler;
        this.configService = configService;
        this.eventParser = new EventParser();
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        var serverTimezone = getServerTimeZone(slashCommandEvent.getGuild().getId());
        switch (EventCommandName.valueOf(slashCommandEvent.getSubcommandName())) {
            case LIST -> slashCommandEvent.reply(listEvents(serverTimezone)).queue();
            case PLAN -> planEvent(slashCommandEvent, serverTimezone);
        }
    }

    private void planEvent(SlashCommandEvent slashCommandEvent, String serverTimezone) {
        Event event = eventParser.parsePlanMessage(slashCommandEvent, slashCommandEvent.getUser()
                .getName(), serverTimezone);
        eventScheduler.addEvent(event, slashCommandEvent.getJDA().getShardManager());
    }

    private Message listEvents(String serverTimezone) {
        var eventList = new ArrayList<Event>();
        eventRepository.findAll().forEach(eventList::add);
        eventList.sort(Comparator.comparing(Event::getStartTime));
        if (eventList.isEmpty()) {
            return new MessageBuilder().append(resourceBundle().getString("command.event.list.noeventsfound")).build();
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(resourceBundle().getString("command.event.list.title"));
        embedBuilder.setColor(BLUE);
        for (var event : eventList) {
            event.setResourceBundle(resourceBundle());
            embedBuilder.addField(event.getName(), event.toString(), true);
        }
        embedBuilder.setFooter(resourceBundle().getString("command.event.list.footer") + serverTimezone, null);
        return new MessageBuilder(embedBuilder).build();
    }

    private String getServerTimeZone(String serverId) {
        return configService.getSingleValueByName(serverId, ConfigConstants.SERVER_TIMEZONE)
                .orElse(ConfigConstants.DEFAULT_TIMEZONE);
    }

    @Override
    public String getName() {
        return "event";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Collections.emptyList();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(new SubcommandData(EventCommandName.PLAN.get(), "Create a new event.")
                        .addOption(OptionType.ROLE, "gamename", "Name of the game the event is used by.", true)
                        .addOption(OptionType.STRING, "name", "Name of the event.", true)
                        .addOption(OptionType.STRING, "starttime", "Date/Time the event starts.", true),
                new SubcommandData(EventCommandName.LIST.get(), "List the events on this server."));
    }
}
