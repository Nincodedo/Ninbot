package dev.nincodedo.ninbot.components.common.message;

import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class ImpersonationController {

    private Impersonation impersonation;
    private Guild guild;
    private TextChannel textChannel;
    private WebhookHelper webhookHelper;
    private String webhookName = "ninbot";

    public ImpersonationController(Impersonation impersonation, Guild guild, TextChannel textChannel) {
        this.impersonation = impersonation;
        this.guild = guild;
        this.textChannel = textChannel;
        this.webhookHelper = new WebhookHelper();
    }

    public void sendMessage(String message) {
        setupWebhook();
        webhookHelper.sendMessage(message);
        tearDown();
    }

    private void tearDown() {
        webhookHelper.getWebhookByName(guild, textChannel, impersonation.name())
                .ifPresent(webhook -> webhook.getManager().setName(webhookName).queue());
    }

    private void setupWebhook() {
        val webhookOptional = webhookHelper.getWebhookByName(guild, textChannel, webhookName);
        if (webhookOptional.isPresent()) {
            webhookHelper.setWebhookIcon(impersonation.iconUrl())
                    .setName(impersonation.name())
                    .complete();
        } else {
            //throws something
        }
    }
}
