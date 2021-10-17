package dev.nincodedo.ninbot.common.message.impersonation;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.WebhookManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
class WebhookHelper {

    private Webhook webhook;

    /**
     * Returns Optional of the webhook if found
     *
     * @param guild       the guild to check for the webhook
     * @param textChannel the text channel to set the webhook to
     * @param name        name of the webhook
     * @return Optional of the webhook if the bot has Permission.MANAGE_WEBHOOKS and its found
     */
    public Optional<Webhook> getWebhookByName(Guild guild, TextChannel textChannel, String name) {
        if (guild.getSelfMember().getPermissions().contains(Permission.MANAGE_WEBHOOKS)) {
            var webhooks = guild.retrieveWebhooks().complete();
            for (var webhook : webhooks) {
                if (webhook.getName().equalsIgnoreCase(name)) {
                    webhook.getManager().setChannel(textChannel).complete();
                    this.webhook = webhook;
                    return Optional.of(webhook);
                }
            }
        }
        return Optional.empty();
    }

    public WebhookManager setWebhookIcon(String iconUrl) {
        try {
            Icon icon = Icon.from(new URL(iconUrl).openStream());
            webhook.getManager().setAvatar(icon).queue();
        } catch (IOException e) {
            log.error("Failed to grab avatar", e);
        }
        return webhook.getManager();
    }

    public @NotNull CompletableFuture<ReadonlyMessage> sendMessage(Message message) {
        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
        messageBuilder.append(message.getContentRaw());
        try (WebhookClient client = new WebhookClientBuilder(webhook.getUrl()).build()) {
            return client.send(messageBuilder.build());
        }
    }
}
