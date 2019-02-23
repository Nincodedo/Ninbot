package com.nincraft.ninbot.components.admin;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AdminCommand extends AbstractCommand {

    private ConfigService configService;

    public AdminCommand(ConfigService configService) {
        length = 3;
        name = "admin";
        checkExactLength = false;
        description = "Admin commands";
        permissionLevel = RolePermission.ADMIN;
        this.configService = configService;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "config-add":
                addConfig(event);
                break;
            case "config-remove":
                removeConfig(event);
                break;
            case "config-list":
                listConfigs(event);
                break;
            default:
                messageUtils.reactUnknownResponse(event.getMessage());
                break;
        }
    }

    private void listConfigs(MessageReceivedEvent event) {
        val configsByServerId = configService.getConfigsByServerId(event.getGuild().getId());
        val serverName = event.getGuild().getName();
        if (configsByServerId.isEmpty()) {
            messageUtils.sendMessage(event.getChannel(), "No configs found for server %s", serverName);
            return;
        }
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Configs for " + serverName);
        for (val config : configsByServerId) {
            embedBuilder.addField(config.getName(), config.getValue(), false);
        }
        messageBuilder.setEmbed(embedBuilder.build());
        messageUtils.sendMessage(event.getChannel(), messageBuilder.build());
    }

    private void removeConfig(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        if (getCommandLength(message) < 5) {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
            return;
        }
        Config config = new Config(event.getGuild().getId(), message.split("\\s+")[3], message.split("\\s+")[4]);
        configService.removeConfig(config);
        messageUtils.reactSuccessfulResponse(event.getMessage());
    }

    private void addConfig(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        if (getCommandLength(message) < 5) {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
            return;
        }
        Config config = new Config(event.getGuild().getId(), message.split("\\s+")[3], message.split("\\s+")[4]);
        val isSuccessful = configService.addConfig(config);
        messageUtils.reactAccordingly(event.getMessage(), isSuccessful);
    }
}
