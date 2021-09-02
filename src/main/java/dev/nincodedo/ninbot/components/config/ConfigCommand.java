package dev.nincodedo.ninbot.components.config;

import dev.nincodedo.ninbot.common.RolePermission;
import dev.nincodedo.ninbot.common.message.MessageAction;
import dev.nincodedo.ninbot.components.command.AbstractCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class ConfigCommand extends AbstractCommand {

    public ConfigCommand() {
        length = 3;
        name = "config";
        checkExactLength = false;
        permissionLevel = RolePermission.ADMIN;
    }

    //TODO implement SlashCommand
    @Override
    protected MessageAction executeCommand(PrivateMessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);

        return messageAction;
    }

    private void updateConfig(String messageString, String guildId) {
        Config config = new Config(guildId, getSubcommandNoTransform(messageString, 3),
                getSubcommandNoTransform(messageString, 4));
        configService.updateConfig(config);
    }

    private Message listConfigs(MessageReceivedEvent event) {
        var configList = configService.getConfigsByServerId(event.getGuild().getId());
        var serverName = event.getGuild().getName();
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
