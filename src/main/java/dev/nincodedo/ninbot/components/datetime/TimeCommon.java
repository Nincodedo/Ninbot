package dev.nincodedo.ninbot.components.datetime;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.util.Map;

@UtilityClass
class TimeCommon {
    static Map<String, TimeFormat> timeFormatMap = Map.of(
            "relative", TimeFormat.RELATIVE,
            "time", TimeFormat.TIME_SHORT,
            "date", TimeFormat.DATE_SHORT,
            "datetime", TimeFormat.DATE_TIME_SHORT
    );
}
