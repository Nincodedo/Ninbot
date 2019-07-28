package com.nincraft.ninbot;

import com.nincraft.ninbot.components.command.CommandResult;

public class TestUtils {
    public static boolean containsEmoji(CommandResult commandResult, String emoji) {
        return commandResult.getEmojisList().contains(emoji);
    }

    public static boolean containsMessage(CommandResult commandResult, String messageText) {
        return commandResult.getChannelMessageList().get(0).getContentRaw().contains(messageText);
    }

    public static boolean containsEmbeddedTitle(CommandResult commandResult, String titleText) {
        return commandResult.getChannelMessageList().get(0).getEmbeds().get(0).getTitle().contains(titleText);
    }

    public static boolean containsEmbeddedName(CommandResult commandResult, String name) {
        return commandResult.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getName()
                .contains(name);
    }

    public static boolean containsEmbeddedValue(CommandResult commandResult, String value) {
        return commandResult.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getValue()
                .contains(value);
    }
}
