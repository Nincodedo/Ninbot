package dev.nincodedo.ninbot.components.haiku;

import eu.crydee.syllablecounter.SyllableCounter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class HaikuMessageParser {

    private SyllableCounter syllableCounter;

    public HaikuMessageParser() {
        this.syllableCounter = new SyllableCounter();
    }

    Optional<String> isHaikuable(String message) {
        if (isMessageOnlyCharacters(message) && getSyllableCount(message) == 17) {
            String[] splitMessage = message.split("\\s+");
            List<String> lines = new ArrayList<>();
            var results = checkLine(0, 5, splitMessage);
            if (results.counter() == -1) {
                return Optional.empty();
            } else {
                lines.add(results.line());
            }
            results = checkLine(results.counter(), 7, splitMessage);
            if (results.counter() == -1) {
                return Optional.empty();
            } else {
                lines.add(results.line());
            }
            results = checkLine(results.counter(), 5, splitMessage);
            if (results.counter() == -1) {
                return Optional.empty();
            } else {
                lines.add(results.line());
            }
            if (results.counter() == splitMessage.length) {
                return Optional.of(lines.get(0) + "\n" + lines.get(1) + "\n" + lines.get(2));
            }
        }
        return Optional.empty();
    }

    HaikuParsingResults checkLine(int counter, int syllables, String[] splitMessage) {
        StringBuilder line = new StringBuilder();
        for (; counter < splitMessage.length; counter++) {
            String word = splitMessage[counter];
            line.append(word);
            line.append(" ");
            int syllableCount = getSyllableCount(line.toString());
            if (syllableCount > syllables) {
                return new HaikuParsingResults(-1, line.toString());
            } else if (syllableCount == syllables) {
                counter++;
                return new HaikuParsingResults(counter, line.toString());
            }
        }
        return new HaikuParsingResults(-1, line.toString());
    }

    boolean isMessageOnlyCharacters(String message) {
        return Pattern.compile("^[a-zA-Z\\sé.,!?]+$").matcher(message).matches();
    }

    int getSyllableCount(String message) {
        int count = 0;
        for (String word : message.split("\\s+")) {
            count += syllableCounter.count(word.replaceAll("\\W", ""));
        }
        return count;
    }
}
