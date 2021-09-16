package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
        var subcommandName = slashCommandEvent.getSubcommandName();
        if (subcommandName == null) {
            return;
        }
        var serverTimezone = getServerTimeZone(slashCommandEvent.getGuild().getId());
        switch (EventCommandName.valueOf(subcommandName)) {
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
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(new SubcommandData(EventCommandName.PLAN.get(), "Create a new event.")
                        .addOption(OptionType.STRING, "name", "The name of the event.", true)
                        .addOption(OptionType.STRING, "date", "The start date of the event, in MM-DD-YYYY format", true)
                        .addOption(OptionType.STRING, "game", "The game associated with the event.")
                        .addOption(OptionType.STRING, "time", "The start time of the event. Defaults to midnight."),
                new SubcommandData(EventCommandName.LIST.get(), "List the events on this server."));
    }
}
