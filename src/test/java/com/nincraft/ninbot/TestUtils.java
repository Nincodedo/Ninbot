package com.nincraft.ninbot;

import com.nincraft.ninbot.components.command.CommandResult;

import java.util.List;

public class TestUtils {
    public static List<String> returnEmoji(CommandResult commandResult) {
        return commandResult.getEmojisList();
    }

    public static String returnMessage(CommandResult commandResult) {
        return commandResult.getChannelMessageList().get(0).getContentRaw();
    }

    public static String returnEmbeddedTitle(CommandResult commandResult) {
        return commandResult.getChannelMessageList().get(0).getEmbeds().get(0).getTitle();
    }

    public static String returnEmbeddedName(CommandResult commandResult) {
        return commandResult.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getName();
    }

    public static String returnEmbeddedValue(CommandResult commandResult) {
        return commandResult.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getValue();
    }

    public static String returnPrivateMessageEmbededName(CommandResult commandResult) {
        return commandResult.getPrivateMessageList().get(0).getEmbeds().get(0).getFields().get(0).getName();
    }

    public static String returnPrivateMessage(CommandResult commandResults) {
        return commandResults.getPrivateMessageList().get(0).getContentRaw();
    }
}
