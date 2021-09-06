package dev.nincodedo.ninbot;

import dev.nincodedo.ninbot.common.message.MessageReceivedEventMessageAction;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public class TestUtils {
    public static List<String> returnEmoji(MessageReceivedEventMessageAction messageAction) {
        return messageAction.getReactions();
    }

    public static String returnMessage(MessageReceivedEventMessageAction messageAction) {
        return messageAction.getChannelMessageList().get(0).getContentRaw();
    }

    public static String returnEmbeddedTitle(MessageReceivedEventMessageAction messageAction) {
        return messageAction.getChannelMessageList().get(0).getEmbeds().get(0).getTitle();
    }

    public static List<MessageEmbed.Field> returnPrivateMessageEmbedFields(
            MessageReceivedEventMessageAction messageAction) {
        return messageAction.getPrivateMessageList().get(0).getEmbeds().get(0).getFields();
    }

    public static String returnEmbeddedName(MessageReceivedEventMessageAction messageAction) {
        return messageAction.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getName();
    }

    public static String returnEmbeddedValue(MessageReceivedEventMessageAction messageAction) {
        return messageAction.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getValue();
    }

    public static String returnPrivateMessageEmbededName(MessageReceivedEventMessageAction messageAction) {
        return messageAction.getPrivateMessageList().get(0).getEmbeds().get(0).getFields().get(0).getName();
    }

    public static String returnPrivateMessage(MessageReceivedEventMessageAction commandResults) {
        return commandResults.getPrivateMessageList().get(0).getContentRaw();
    }
}
