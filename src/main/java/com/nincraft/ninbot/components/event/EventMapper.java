package com.nincraft.ninbot.components.event;

import lombok.extern.log4j.Log4j2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Log4j2
public class EventMapper {

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public Event mapEvent(ResultSet resultSet) {
        Event event = new Event();
        try {
            event.setId(resultSet.getInt("Id"));
            event.setName(resultSet.getString("Name"));
            event.setAuthorName(resultSet.getString("AuthorName"));
            event.setGameName(resultSet.getString("GameName"));
            event.setDescription(resultSet.getString("Description"));
            try {
                event.setStartTime(OffsetDateTime.parse(resultSet.getString("StartTime"), formatter));
            } catch (DateTimeParseException e) {
                event.setStartTime(OffsetDateTime.parse(resultSet.getString("StartTime"), DateTimeFormatter.ISO_DATE_TIME));
            }
            if (resultSet.getString("EndTime") != null) {
                try {
                    event.setStartTime(OffsetDateTime.parse(resultSet.getString("EndTime"), formatter));
                } catch (DateTimeParseException e) {
                    event.setStartTime(OffsetDateTime.parse(resultSet.getString("EndTime"), DateTimeFormatter.ISO_DATE_TIME));
                }
            }
            event.setHidden(resultSet.getInt("Hidden"));
        } catch (SQLException e) {
            log.error("Failed to map event", e);
        }
        return event;
    }
}
