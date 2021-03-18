package dev.nincodedo.ninbot.components.common.message;

public final class MessageUtils {
    private MessageUtils() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static boolean isSpoiler(String message) {
        String checkMessage = message.replaceFirst("\\|\\|", "");
        return checkMessage.contains("||");
    }

    /**
     * Puts spoiler tags around an entire string if the raw message had any spoiler tags
     *
     * @param message    the content stripped string
     * @param rawMessage the raw content string
     * @return string with spoiler tags around it if the raw content had any spoiler tags in it
     */
    public static String addSpoilerText(String message, String rawMessage) {
        String newMessage = message;
        if (isSpoiler(rawMessage)) {
            newMessage = "||" + newMessage + "||";
        }
        return newMessage;
    }
}
