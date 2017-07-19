package com.nincraft.ninbot.db;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SqlConstants {
    public static final String INSERT_EVENT = "INSERT INTO GameEvents(Name, AuthorName, GameName, Description, StartTime, EndTime, Hidden) VALUES(?,?,?,?,?,?,?)";
    public static final String GET_ALL_EVENTS = "SELECT * FROM GameEvents where Hidden = 0";
    public static final String UPDATE_HIDE_EVENT = "UPDATE GameEvents set Hidden = 1 where Id = ?";
}
