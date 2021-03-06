package dev.nincodedo.ninbot.components.common.message;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.managers.WebhookManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Log4j2
public class WebhookHelper {

    @Getter
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
            val webhooks = guild.retrieveWebhooks().complete();
            for (val webhook : webhooks) {
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

    public void sendMessage(String message) {
        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
        messageBuilder.append(message);
        try (WebhookClient client = new WebhookClientBuilder(webhook.getUrl()).build()) {
            client.send(messageBuilder.build()).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send webhook message", e);
        }
    }
}
