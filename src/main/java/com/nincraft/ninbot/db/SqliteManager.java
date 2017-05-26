package com.nincraft.ninbot.db;

import com.nincraft.ninbot.util.Reference;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Log4j2
public class SqliteManager {
    public void setupDb() {
        try (Connection connection = DriverManager.getConnection(Reference.SQLITE_DB);
             Statement statement = connection.createStatement()) {
            statement.execute(SqlConstants.CREATE_EVENT_TABLE);
        } catch (SQLException e) {
            log.error("", e);
        }
    }
}
