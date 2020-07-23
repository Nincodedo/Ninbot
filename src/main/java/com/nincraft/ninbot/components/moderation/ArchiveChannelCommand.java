package com.nincraft.ninbot.components.moderation;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class ArchiveChannelCommand extends AbstractCommand {

    public ArchiveChannelCommand() {
        name = "archive-channel";
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
        configService.getSingleValueByName(event.getGuild().getId(), ConfigConstants.ARCHIVE_CATEGORY_ID)
                .ifPresent(archiveChannelId -> moveChannel(archiveChannelId, textChannel, guild));
        return messageAction;
    }

    private TextChannel getChannelFromMessage(String message, MessageReceivedEvent event) {
        val commandLength = getCommandLength(message);
        TextChannel textChannel = null;
        //archive this channel
        if (commandLength == 2) {
            textChannel = event.getTextChannel();
        }
        //archive some named channel
        else if (commandLength == 3) {
            textChannel = event.getJDA().getTextChannelById(getSubcommand(message, 2));
        }
        return textChannel;
    }

    private void moveChannel(String archiveChannelId, TextChannel textChannel, Guild guild) {
        guild.getCategoryById(archiveChannelId)
                .modifyTextChannelPositions()
                .selectPosition(textChannel.getManager().getChannel())
                .moveTo(0)
                .queue();
    }
}
