package dev.nincodedo.nincord.message.impersonation;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.WebhookManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Slf4j
class WebhookHelper {

    private Webhook webhook;

    /**
     * Returns Optional of the webhook if found.
     *
     * @param guild          the guild to check for the webhook
     * @param messageChannel the message channel to set the webhook to
     * @param name           name of the webhook
     * @return Optional of the webhook if the bot has Permission.MANAGE_WEBHOOKS and its found
     */
    public Optional<Webhook> getWebhookByName(Guild guild, MessageChannel messageChannel, String name) {
        if (guild.getSelfMember().getPermissions().contains(Permission.MANAGE_WEBHOOKS)) {
            var webhooks = guild.retrieveWebhooks().complete();
            for (var hook : webhooks) {
                if (hook.getName().equalsIgnoreCase(name) && messageChannel instanceof TextChannel textChannel) {
                    hook.getManager().setChannel(textChannel).complete();
                    webhook = hook;
                    return Optional.of(hook);
                }
            }
        }
        return Optional.empty();
    }

    public WebhookManager setWebhookIcon(String iconUrl) {
        try {
            Icon icon = Icon.from(new URI(iconUrl).toURL().openStream());
            webhook.getManager().setAvatar(icon).queue();
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to grab avatar", e);
        }
        return webhook.getManager();
    }

    public void sendMessage(MessageCreateData message) {
        if (webhook != null) {
            var client = WebhookClient.createClient(webhook.getJDA(), webhook.getUrl());
            client.sendMessage(message).queue();
        }
    }
}
