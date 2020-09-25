package dev.nincodedo.ninbot.components.moderation;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.MessageAction;
import dev.nincodedo.ninbot.components.common.RolePermission;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.ChannelManager;
import org.springframework.stereotype.Component;

@Component
public class ArchiveChannelCommand extends AbstractCommand {

    public ArchiveChannelCommand() {
        name = "archive";
        length = 2;
        checkExactLength = false;
        permissionLevel = RolePermission.MODS;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        TextChannel textChannel = getChannelFromMessage(message, event);
        //what are you doin
        if (textChannel == null) {
            messageAction.addUnsuccessfulReaction();
            return messageAction;
        }
        val guild = event.getGuild();

        String archiveCategoryId = getCategoryIdMovingTo(event, guild);
        if (archiveCategoryId == null) {
            messageAction.addUnsuccessfulReaction();
            return messageAction;
        }
        moveChannelToCategory(archiveCategoryId, textChannel, guild).queue(
                aVoidSuccess -> updateChannelPermissions(event, textChannel.getId(), archiveCategoryId),
                aVoidFailure -> MessageAction.unsuccessfulReaction(event.getMessage()));
        return messageAction;
    }

    void updateChannelPermissions(MessageReceivedEvent event, String textChannelId, String targetCategoryId) {
        val targetCategory = event.getJDA().getCategoryById(targetCategoryId);
        event.getJDA()
                .getTextChannelById(textChannelId)
                .getManager()
                .sync(targetCategory)
                .queue(aVoidSuccess -> MessageAction.successfulReaction(event.getMessage()));
    }

    String getCategoryIdMovingTo(MessageReceivedEvent event, Guild guild) {
        String archiveCategoryId;
        val categoryIdOptional = configService.getSingleValueByName(event.getGuild()
                .getId(), ConfigConstants.ARCHIVE_CATEGORY_ID);
        //an archive channel has been already configured
        if (categoryIdOptional.isPresent()) {
            archiveCategoryId = categoryIdOptional.get();
        } else {
            val categories = guild.getCategoriesByName("archive", true);
            if (!categories.isEmpty()) {
                archiveCategoryId = categories.get(0).getId();
            } else {
                archiveCategoryId = null;
            }
        }
        return archiveCategoryId;
    }

    private TextChannel getChannelFromMessage(String message, MessageReceivedEvent event) {
        val commandLength = getCommandLength(message);
        TextChannel textChannel = null;
        //this channel
        if (commandLength == 2) {
            textChannel = event.getTextChannel();
        }
        //some named channel
        else if (commandLength == 3) {
            val mentionedChannels = event.getMessage().getMentionedChannels();
            if (mentionedChannels.isEmpty()) {
                textChannel = event.getJDA().getTextChannelsByName(getSubcommand(message, 2), true).get(0);
            } else {
                return mentionedChannels.get(0);
            }
        }
        return textChannel;
    }

    private ChannelManager moveChannelToCategory(String archiveCategoryId, TextChannel textChannel, Guild guild) {
        val archiveCategory = guild.getCategoryById(archiveCategoryId);
        return textChannel.getManager().setParent(archiveCategory);
    }
}
