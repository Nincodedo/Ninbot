package dev.nincodedo.ninbot;

import dev.nincodedo.ninbot.common.message.MessageReceivedEventMessageExecutor;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public class TestUtils {
    public static List<String> returnEmoji(MessageReceivedEventMessageExecutor messageAction) {
        return messageAction.getReactions();
    }

    public static String returnMessage(MessageReceivedEventMessageExecutor messageAction) {
        return messageAction.getChannelMessageList().get(0).getContentRaw();
    }

    public static String returnEmbeddedTitle(MessageReceivedEventMessageExecutor messageAction) {
        return messageAction.getChannelMessageList().get(0).getEmbeds().get(0).getTitle();
    }

    public static List<MessageEmbed.Field> returnPrivateMessageEmbedFields(
            MessageReceivedEventMessageExecutor messageAction) {
        return messageAction.getPrivateMessageList().get(0).getEmbeds().get(0).getFields();
    }

    public static String returnEmbeddedName(MessageReceivedEventMessageExecutor messageAction) {
        return messageAction.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getName();
    }

    public static String returnEmbeddedValue(MessageReceivedEventMessageExecutor messageAction) {
        return messageAction.getChannelMessageList()
                .get(0)
                .getEmbeds()
                .get(0)
                .getFields()
                .get(0)
                .getValue();
    }

    public static String returnPrivateMessageEmbededName(MessageReceivedEventMessageExecutor messageAction) {
        return messageAction.getPrivateMessageList().get(0).getEmbeds().get(0).getFields().get(0).getName();
    }

    public static String returnPrivateMessage(MessageReceivedEventMessageExecutor commandResults) {
        return commandResults.getPrivateMessageList().get(0).getContentRaw();
    }
}
