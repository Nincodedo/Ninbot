package dev.nincodedo.nincord.message.impersonation;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.WebhookManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

    public @NotNull CompletableFuture<ReadonlyMessage> sendMessage(MessageCreateData message) {
        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
        messageBuilder.append(message.getContent());
        try (WebhookClient client = new WebhookClientBuilder(webhook.getUrl()).build()) {
            return client.send(messageBuilder.build());
        }
    }
}
