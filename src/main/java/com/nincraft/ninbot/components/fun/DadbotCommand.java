package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigDao;
import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.RolePermission;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public class DadbotCommand extends AbstractCommand {

    private ConfigDao configDao;

    public DadbotCommand(ConfigDao configDao) {
        name = "dad";
        length = 3;
        checkExactLength = false;
        permissionLevel = RolePermission.ADMIN;
        hidden = true;
        this.configDao = configDao;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "blacklist":
                blacklistChannel(event.getGuild().getId(), event.getChannel(), event.getMessage());
                break;
            default:
                break;
        }
    }

    private void blacklistChannel(String serverId, MessageChannel channel, Message message) {
        val configChannelBlacklist = configDao.getConfigByName(serverId, "dadbotChannelBlacklist");
        List<String> channels = configChannelBlacklist.stream().map(Config::getValue).collect(Collectors.toList());
        if (!channels.contains(channel.getId())) {
            configDao.addConfig(serverId, "dadbotChannelBlacklist", channel.getId());
            MessageUtils.reactSuccessfulResponse(message);
        }
    }
}
