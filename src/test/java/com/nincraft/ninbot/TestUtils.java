package com.nincraft.ninbot;

import com.nincraft.ninbot.components.common.MessageAction;

import java.util.List;

public class TestUtils {
    public static List<String> returnEmoji(MessageAction messageAction) {
        return messageAction.getEmojisList();
    }

    public static String returnMessage(MessageAction messageAction) {
        return messageAction.getChannelMessageList().get(0).getContentRaw();
    }

    public static String returnEmbeddedTitle(MessageAction messageAction) {
        return messageAction.getChannelMessageList().get(0).getEmbeds().get(0).getTitle();
    }

    public static String returnEmbeddedName(MessageAction messageAction) {
        return messageAction.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getName();
    }

    public static String returnEmbeddedValue(MessageAction messageAction) {
        return messageAction.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getValue();
    }

    public static String returnPrivateMessageEmbededName(MessageAction messageAction) {
        return messageAction.getPrivateMessageList().get(0).getEmbeds().get(0).getFields().get(0).getName();
    }

    public static String returnPrivateMessage(MessageAction commandResults) {
        return commandResults.getPrivateMessageList().get(0).getContentRaw();
    }
}
