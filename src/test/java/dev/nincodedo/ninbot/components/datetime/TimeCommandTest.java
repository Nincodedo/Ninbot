package dev.nincodedo.ninbot.components.datetime;

import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.Timestamp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimeCommandTest {

    TimeCommand timeCommand = new TimeCommand() {
        @Override
        protected Timestamp now(TimeFormat timeFormat) {
            return timeFormat.atTimestamp(1641016800000L);
        }
    };

    static List<TimestampTest> timestamps() {
        return List.of(
                new TimestampTest(100.0, "hours", "relative", TimeFormat.RELATIVE, 1641376800000L),
                new TimestampTest(5.0, "days", "date", TimeFormat.DATE_SHORT, 1641448800000L)
        );
    }

    @ParameterizedTest
    @MethodSource("timestamps")
    void getTimestamp(TimestampTest timestampTest) {
        Timestamp timestamp = timeCommand.getTimestamp(timestampTest.amount(), timestampTest.unit(),
                timestampTest.display());
        assertThat(timestamp.getFormat()).isEqualTo(timestampTest.format());
        assertThat(timestamp.getTimestamp()).isEqualTo(timestampTest.timestamp());
    }
}

record TimestampTest(Double amount, String unit, String display, TimeFormat format, Long timestamp) {
}
