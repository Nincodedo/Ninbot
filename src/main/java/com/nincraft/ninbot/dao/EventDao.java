package com.nincraft.ninbot.dao;

import com.nincraft.ninbot.container.Event;
import com.nincraft.ninbot.db.SqlConstants;
import com.nincraft.ninbot.mapper.EventMapper;
import com.nincraft.ninbot.util.Reference;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class EventDao implements IEventDao {

    private EventMapper eventMapper;

    public EventDao(){
        this.eventMapper = new EventMapper();
    }

    @Override
    public void addEvent(Event event) {
        Connection connection = connect();
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(SqlConstants.INSERT_EVENT)) {
                int i = 1;
                statement.setString(i++, event.getName());
                statement.setString(i++, event.getAuthorName());
                statement.setString(i++, event.getGameName());
                statement.setString(i++, event.getDescription());
                statement.setString(i++, event.getStartTime().toString());
                statement.setString(i, event.getEndTime().toString());
                statement.execute();
            } catch (SQLException e) {
                log.error("SQL Exception during add event", e);
            } finally {
                close(connection);
            }
        }
    }

    private void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Error when closing sqlite connection", e);
        }
    }

    @Override
    public void removeEvent(Event event) {
        Connection connection = connect();
        if(connection!=null){
            try (PreparedStatement statement = connection.prepareStatement(SqlConstants.UPDATE_HIDE_EVENT)){
                statement.setInt(1, event.getId());
                statement.execute();
            } catch (SQLException e) {
                log.error("Failed to remove event", e);
            } finally {
                close(connection);
            }
        }
    }

    @Override
    public List<Event> getAllEvents() {
        Connection connection = connect();
        List<Event> results = new ArrayList<>();
        if (connection != null) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SqlConstants.GET_ALL_EVENTS)) {
                while (resultSet.next()) {
                    results.add(eventMapper.mapEvent(resultSet));
                }
            } catch (SQLException e) {
                log.error("Failed to get all events", e);
                return results;
            }
            close(connection);
        }
        return results;
    }

    @Override
    public Event getEventByName(String name) {
        //TODO
        return null;
    }

    @Override
    public Event getEventByAuthorName(String authorName) {
        //TODO
        return null;
    }

    private Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Reference.SQLITE_DB);
        } catch (SQLException e) {
            log.error("Error connecting to sqlite", e);
        }
        return connection;
    }
}
