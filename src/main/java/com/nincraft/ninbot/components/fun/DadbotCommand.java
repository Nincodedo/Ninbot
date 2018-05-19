package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.RolePermission;
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
        permissionLevel = RolePermission.ADMIN;
        hidden = true;
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
                break;
        }
    }

    private void toggleBlacklistChannel(String serverId, MessageChannel channel, Message message) {
        String configName = "dadbotChannelBlacklist";
        val channelBlacklist = configService.getValuesByName(serverId, configName);
        if (!channelBlacklist.contains(channel.getId())) {
            configService.addConfig(serverId, configName, channel.getId());
        } else {
            configService.removeConfig(serverId, configName, channel.getId());
        }
        MessageUtils.reactSuccessfulResponse(message);
    }
}
