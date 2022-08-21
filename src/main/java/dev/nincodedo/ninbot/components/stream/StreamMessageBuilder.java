package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.common.message.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ResourceBundle;

@Slf4j
public class StreamMessageBuilder {

    MessageCreateData buildStreamAnnounceMessage(Member member,
            String streamingUrl, String gameName, String streamTitle, Guild guild) {
        log.trace("Building stream announce message for {} server {}", FormatLogObject.memberInfo(member),
                FormatLogObject.guildName(guild));
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        EmbedBuilder embedBuilder;
        if (!streamingUrl.contains("https://")) {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(String.format(resourceBundle.getString("listener.stream.announce.voicechannel"),
                            member.getEffectiveName(), streamingUrl), null, member.getEffectiveAvatarUrl())
                    .setTitle(streamTitle);
        } else {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(String.format(resourceBundle.getString("listener.stream.announce"),
                            member.getEffectiveName(), gameName,
                            streamingUrl), streamingUrl, member.getEffectiveAvatarUrl())
                    .setTitle(streamTitle);
        }
        embedBuilder.setColor(MessageUtils.getColor(member.getEffectiveAvatarUrl()));
        return new MessageCreateBuilder().addEmbeds(embedBuilder.build()).build();
    }
}
