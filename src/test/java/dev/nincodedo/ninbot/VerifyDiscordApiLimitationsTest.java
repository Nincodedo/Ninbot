package dev.nincodedo.ninbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

class VerifyDiscordApiLimitationsTest {

    @Test
    void testCommandDescriptions() {
        List<String> failingKeys = new ArrayList<>();
        var bundle = ResourceBundle.getBundle("lang", Locale.ENGLISH);
        var keys = bundle.getKeys();
        int count = 0;
        while (keys.hasMoreElements()) {
            var key = keys.nextElement();
            count = getCommandDescriptions(failingKeys, bundle.getString(key), count, key);
        }
        if (!failingKeys.isEmpty()) {
            Assertions.fail(failingKeys.toString());
        }
        assertThat(count).isPositive();
    }

    private int getCommandDescriptions(List<String> failingKeys, String checkString, int count, String key) {
        if (key.contains(".description")) {
            count++;
            if (checkString.length() > 100 || checkString.length() < 1) {
                failingKeys.add(key);
            }
        }
        return count;
    }
}
