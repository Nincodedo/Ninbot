package com.nincraft.ninbot.mapper;

import com.nincraft.ninbot.container.Event;
import lombok.extern.log4j.Log4j2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            event.setStartTime(LocalDateTime.parse(resultSet.getString("StartTime"), formatter));
            event.setEndTime(LocalDateTime.parse(resultSet.getString("EndTime"), formatter));
            event.setHidden(resultSet.getInt("Hidden"));
        } catch (SQLException e) {
            log.error("Failed to map event", e);
        }
        return event;
    }
}
