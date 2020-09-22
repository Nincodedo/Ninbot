package com.nincraft.ninbot.components.config;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.common.RolePermission;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ConfigCommand extends AbstractCommand {


    public ConfigCommand() {
        length = 3;
        name = "config";
        checkExactLength = false;
        permissionLevel = RolePermission.ADMIN;
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "add":
                if (getCommandLength(message) >= 5) {
                    addConfig(message, event.getGuild().getId());
                    messageAction.addSuccessfulReaction();
                } else {
                    messageAction.addUnsuccessfulReaction();
                }
                break;
            case "remove":
                if (getCommandLength(message) >= 5) {
                    removeConfig(message, event.getGuild().getId());
                    messageAction.addSuccessfulReaction();
                } else {
                    messageAction.addUnsuccessfulReaction();
                }
                break;
            case "update":
                if (getCommandLength(message) >= 5) {
                    updateConfig(message, event.getGuild().getId());
                    messageAction.addSuccessfulReaction();
                } else {
                    messageAction.addUnsuccessfulReaction();
                }
                break;
            case "list":
                messageAction.addChannelAction(listConfigs(event));
                break;
            default:
                messageAction.addUnknownReaction();
                break;
        }
        return messageAction;
    }

    private void updateConfig(String messageString, String guildId) {
        Config config = new Config(guildId, getSubcommandNoTransform(messageString, 3),
                getSubcommandNoTransform(messageString, 4));
        configService.updateConfig(config);
    }

    private Message listConfigs(MessageReceivedEvent event) {
        val configList = configService.getConfigsByServerId(event.getGuild().getId());
        val serverName = event.getGuild().getName();
        if (configList.isEmpty()) {
            return new MessageBuilder().appendFormat(resourceBundle.getString("command.config.noconfigfound"),
                    serverName)
                    .build();
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(resourceBundle.getString("command.config.list.title") + " " + serverName);
        configList.forEach(config -> embedBuilder.addField(config.getName(), config.getValue(), false));
        return new MessageBuilder(embedBuilder).build();
    }

    private void removeConfig(String messageString, String guildId) {
        Config config = new Config(guildId, getSubcommandNoTransform(messageString, 3),
                getSubcommandNoTransform(messageString, 4));
        configService.removeConfig(config);
    }

    private void addConfig(String messageString, String guildId) {
        Config config = new Config(guildId, getSubcommandNoTransform(messageString, 3),
                getSubcommandNoTransform(messageString, 4));
        configService.addConfig(config);
    }
}
