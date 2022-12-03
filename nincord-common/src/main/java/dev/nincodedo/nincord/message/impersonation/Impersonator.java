package dev.nincodedo.nincord.message.impersonation;

import club.minnced.discord.webhook.receive.ReadonlyMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.concurrent.CompletableFuture;

public class Impersonator {

    private Impersonation impersonation;
    private Guild guild;
    private MessageChannel messageChannel;
    private WebhookHelper webhookHelper;
    private String webhookName = "ninbot";

    public Impersonator(Impersonation impersonation, Guild guild, MessageChannel messageChannel) {
        this.impersonation = impersonation;
        this.guild = guild;
        this.messageChannel = messageChannel;
        this.webhookHelper = new WebhookHelper();
    }

    public CompletableFuture<ReadonlyMessage> sendMessage(MessageCreateData message) {
        setupWebhook();
        var future = webhookHelper.sendMessage(message);
        tearDown();
        return future;
    }

    private void tearDown() {
        webhookHelper.getWebhookByName(guild, messageChannel, impersonation.name())
                .ifPresent(webhook -> webhook.getManager().setName(webhookName).queue());
    }

    private void setupWebhook() {
        var webhookOptional = webhookHelper.getWebhookByName(guild, messageChannel, webhookName);
        if (webhookOptional.isPresent()) {
            webhookHelper.setWebhookIcon(impersonation.iconUrl())
                    .setName(impersonation.name())
                    .complete();
        }
    }
}
