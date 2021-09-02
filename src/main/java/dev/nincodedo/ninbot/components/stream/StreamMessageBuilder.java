package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.message.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
public class StreamMessageBuilder {

    Message buildStreamAnnounceMessage(String avatarUrl, String username,
            String streamingUrl, String gameName, String streamTitle, String serverId, Locale locale) {
        log.trace("Building stream announce message for {} server {}", username, serverId);
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(locale);
        EmbedBuilder embedBuilder;
        if (!streamingUrl.contains("https://")) {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(String.format(resourceBundle.getString("listener.stream.announce.voicechannel"),
                            username, streamingUrl), null, avatarUrl)
                    .setTitle(streamTitle);
        } else {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(String.format(resourceBundle.getString("listener.stream.announce"), username, gameName,
                            streamingUrl), streamingUrl, avatarUrl)
                    .setTitle(streamTitle);
        }
        embedBuilder.setColor(MessageUtils.getColor(avatarUrl));
        return new MessageBuilder(embedBuilder).build();
    }
}
