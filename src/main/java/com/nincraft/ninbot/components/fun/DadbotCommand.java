package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.config.ConfigDao;
import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.RolePermission;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DadbotCommand extends AbstractCommand {

    private ConfigDao configDao;

    public DadbotCommand(ConfigDao configDao) {
        name = "dad";
        length = 3;
        checkExactLength = false;
        permissionLevel = RolePermission.ADMIN;
        hidden = true;
        description = "Dad";
        this.configDao = configDao;
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
        val channelBlacklist = configDao.getValuesByName(serverId, configName);
        if (!channelBlacklist.contains(channel.getId())) {
            configDao.addConfig(serverId, configName, channel.getId());
        } else {
            configDao.removeConfig(serverId, configName, channel.getId());
        }
        MessageUtils.reactSuccessfulResponse(message);
    }
}
