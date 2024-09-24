package dev.nincodedo.ninbot.components.dad;

import dev.nincodedo.nincord.message.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
public class DadbotMessageParser {

    public Optional<String> dadReply(String message, String rawMessage) {
        var initialPattern = Pattern.compile("i(?:['‛’‵‘′`]?m| am) ", Pattern.CASE_INSENSITIVE);
        var initialMatcher = initialPattern.matcher(message);
        if (!initialMatcher.find()) {
            log.debug("Failed initial matching \"{}\"", message);
            return Optional.empty();
        }
        var startingMessage = message.substring(initialMatcher.end());
        var endingPunctuationPattern = Pattern.compile("(?<!Mrs?|M[sx]|[SJD]r)[.?!](:? )?");
        var matcher = endingPunctuationPattern.matcher(startingMessage);
        if (matcher.find()) {
            startingMessage = startingMessage.substring(0, matcher.start());
        }

        if (!startingMessage.isEmpty()) {
            return Optional.of(MessageUtils.addSpoilerText(startingMessage, rawMessage));
        } else {
            return Optional.empty();
        }
    }
}
