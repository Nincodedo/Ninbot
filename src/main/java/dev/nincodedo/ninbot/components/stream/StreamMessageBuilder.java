package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.logging.UtilLogging;
import dev.nincodedo.ninbot.common.message.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ResourceBundle;

@Slf4j
public class StreamMessageBuilder {

    Message buildStreamAnnounceMessage(Member member,
            String streamingUrl, String gameName, String streamTitle, Guild guild) {
        log.trace("Building stream announce message for {} server {}", UtilLogging.logMemberInfo(member),
                UtilLogging.logGuildName(guild));
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild.getLocale());
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
        return new MessageBuilder(embedBuilder).build();
    }
}
