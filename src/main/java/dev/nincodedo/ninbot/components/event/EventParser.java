package dev.nincodedo.ninbot.components.event;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.ZoneId;

import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

class EventParser {

    Event parsePlanMessage(SlashCommandEvent slashCommandEvent, String author, String serverTimezone) {
        Event event = new Event();
        event.setAuthorName(author)
                .setGameName(slashCommandEvent.getOption("gamename").getAsRole().getName())
                .setName(slashCommandEvent.getOption("name").getAsString())
                .setStartTime(parse(slashCommandEvent.getOption("starttime").getAsString(), ISO_OFFSET_DATE_TIME)
                        .withZoneSameLocal(ZoneId.of(serverTimezone)))
                .setServerId(slashCommandEvent.getGuild().getId());
        return event;
    }
}
