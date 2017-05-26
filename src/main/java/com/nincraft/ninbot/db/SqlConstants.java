package com.nincraft.ninbot.db;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SqlConstants {
    static final String CREATE_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS GameEvents (Id integer PRIMARY KEY, " +
            "Name text NOT NULL, AuthorName text NOT NULL, GameName text NOT NULL, Description text, " +
            "StartTime text NOT NULL, EndTime text);";
    public static final String INSERT_EVENT = "INSERT INTO GameEvents(Name, AuthorName, GameName, Description, StartTime, EndTime) VALUES(?,?,?,?,?,?)";
    public static final String GET_ALL_EVENTS = "SELECT * FROM GameEvents";
}
