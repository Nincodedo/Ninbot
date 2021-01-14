package dev.nincodedo.ninbot;

import dev.nincodedo.ninbot.components.common.message.MessageAction;
import net.dv8tion.jda.api.entities.MessageEmbed;

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

    public static List<MessageEmbed.Field> returnPrivateMessageEmbedFields(MessageAction messageAction) {
        return messageAction.getPrivateMessageList().get(0).getEmbeds().get(0).getFields();
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
