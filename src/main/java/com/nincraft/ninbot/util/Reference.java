package com.nincraft.ninbot.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Reference {
    public static final String NINBOT_PROPERTIES = "ninbot.properties";
    public static final String OCW_SERVER_ID = "109466144993210368";
    public static final String OCW_EVENT_ANNOUNCE_CHANNEL = "205856796781445120";
    public static final String OCW_DEBUG_CHANNEL = "308081429051604992";
    public static final String SQLITE_DB = "jdbc:sqlite:data/events.db";
    @Getter
    private static final List<String> roleBlacklist = new ArrayList<>(Arrays.asList("admin", "mods", "AIRHORN SOLUTIONS", "@everyone", "Dad Bot"));
}
