package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class DadbotCommand extends AbstractCommand {

    private ConfigService configService;

    public DadbotCommand(ConfigService configService) {
        name = "dad";
        length = 3;
        checkExactLength = false;
        permissionLevel = RolePermission.MODS;
        description = "Dad";
        this.configService = configService;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "toggle":
                toggleBlacklistChannel(event.getGuild().getId(), event.getChannel(), event.getMessage());
                break;
            default:
                messageUtils.reactUnknownResponse(event.getMessage());
                break;
        }
    }

    private void toggleBlacklistChannel(String serverId, MessageChannel channel, Message message) {
        val channelBlacklist = configService.getValuesByName(serverId, ConfigConstants.DADBOT_BLACKLIST_CHANNEL);
        if (!channelBlacklist.contains(channel.getId())) {
            configService.addConfig(serverId, ConfigConstants.DADBOT_BLACKLIST_CHANNEL, channel.getId());
        } else {
            configService.removeConfig(serverId, ConfigConstants.DADBOT_BLACKLIST_CHANNEL, channel.getId());
        }
        messageUtils.reactSuccessfulResponse(message);
    }
}
