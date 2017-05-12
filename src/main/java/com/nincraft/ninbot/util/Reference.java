package com.nincraft.ninbot.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Reference {
    @Getter
    private static final List<String> roleBlacklist = new ArrayList<>(Arrays.asList("admin", "mods", "AIRHORN SOLUTIONS", "@everyone"));
    public static final String NINBOT_PROPERTIES = "ninbot.properties";
}
