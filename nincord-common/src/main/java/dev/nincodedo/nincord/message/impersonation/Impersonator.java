package dev.nincodedo.nincord.message.impersonation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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

    public void sendMessage(MessageCreateData message) {
        setupWebhook();
        webhookHelper.sendMessage(message);
        tearDown();
    }

    private void tearDown() {
        webhookHelper.getWebhookByName(guild, messageChannel, impersonation.name())
                .ifPresent(webhook -> webhook.getManager().setName(webhookName).queue());
    }

    private void setupWebhook() {
        webhookHelper.getWebhookByName(guild, messageChannel, webhookName)
                .ifPresent(_ -> webhookHelper.setWebhookIcon(impersonation.iconUrl())
                        .setName(impersonation.name())
                        .complete());
    }
}
