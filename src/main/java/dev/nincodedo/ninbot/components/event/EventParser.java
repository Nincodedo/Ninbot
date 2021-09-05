package dev.nincodedo.ninbot.components.event;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

class EventParser {

    Event parsePlanMessage(SlashCommandEvent slashCommandEvent, String author, String serverTimezone) {
        Event event = new Event();
        event.setAuthorName(author)
                .setName(slashCommandEvent.getOption("name").getAsString())
                .setStartTime(parse(
                        slashCommandEvent.getOption("date").getAsString() + "T"
                                + getTime(slashCommandEvent.getOption("time")) + serverTimezone, ISO_OFFSET_DATE_TIME))
                .setServerId(slashCommandEvent.getGuild().getId());
        setGameName(event, slashCommandEvent);
        return event;
    }

    private void setGameName(Event event, SlashCommandEvent slashCommandEvent) {
        var gameName = slashCommandEvent.getOption("game");
        if (gameName != null) {
            event.setGameName(gameName.getAsString());
        }
    }

    private String getTime(OptionMapping time) {
        return time != null ? time.getAsString() : "12:00:00";
    }
}
