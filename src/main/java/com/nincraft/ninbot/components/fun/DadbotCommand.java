package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class DadbotCommand extends AbstractCommand {

    private ConfigService configService;

    public DadbotCommand(ConfigService configService) {
        name = "dad";
        length = 3;
        checkExactLength = false;
        permissionLevel = RolePermission.MODS;
        this.configService = configService;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        toggleBlacklistChannel(event.getGuild().getId(), event.getChannel().getId());
        commandResult.addSuccessfulReaction();
        return commandResult;
    }

    private void toggleBlacklistChannel(String serverId, String channelId) {
        val channelBlacklist = configService.getValuesByName(serverId, ConfigConstants.DADBOT_BLACKLIST_CHANNEL);
        if (!channelBlacklist.contains(channelId)) {
            configService.addConfig(serverId, ConfigConstants.DADBOT_BLACKLIST_CHANNEL, channelId);
        } else {
            configService.removeConfig(serverId, ConfigConstants.DADBOT_BLACKLIST_CHANNEL, channelId);
        }
    }
}
