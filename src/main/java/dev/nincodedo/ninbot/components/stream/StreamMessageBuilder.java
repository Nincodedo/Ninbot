package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.common.message.MessageUtils;
import dev.nincodedo.ninbot.components.stream.banner.GameBanner;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerBuilder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class StreamMessageBuilder {

    private GameBannerBuilder gameBannerBuilder;

    StreamMessageBuilder(GameBannerBuilder gameBannerBuilder) {
        this.gameBannerBuilder = gameBannerBuilder;
    }

    MessageCreateData buildStreamAnnounceMessage(Member member,
            String streamingUrl, String gameName, String streamTitle, Guild guild) {
        log.trace("Building stream announce message for {} server {} with game {}", FormatLogObject.memberInfo(member),
                FormatLogObject.guildName(guild), gameName);
        var futureGameBanner = gameBannerBuilder.getGameBannerAsync(gameName);
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        EmbedBuilder embedBuilder = getEmbedBuilder(member, streamingUrl, gameName, streamTitle, resourceBundle);
        embedBuilder.setColor(MessageUtils.getColor(member.getEffectiveAvatarUrl())).setTitle(streamTitle);
        try {
            var gameBanner = futureGameBanner.get();
            if (gameBanner != null) {
                log.trace("Got a game banner! {}", gameBanner);
                embedBuilder.setImage("attachment://" + gameBanner.getFileName());
                embedBuilder.setFooter("How did Ninbot do with this generated banner? Leave some feedback with the "
                        + "buttons below!");
                return new MessageCreateBuilder().addEmbeds(embedBuilder.build())
                        .addFiles(FileUpload.fromData(gameBanner.getFile()))
                        .addComponents(getComponentsForGameBanner(gameBanner))
                        .build();
            }
        } catch (InterruptedException e) {
            log.error("Failed to get a game banner", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error("Failed to get a game banner", e);
        } catch (Exception e) {
            log.error("Something failed with game banners", e);
        }
        log.trace("Game banner didn't work out, building a boring message");
        return buildBoringStreamAnnounceMessage(member, streamingUrl, gameName, streamTitle, guild);
    }

    private ActionRow getComponentsForGameBanner(GameBanner gameBanner) {
        return ActionRow.of(
                Button.primary(String.format("banner-good-%s", gameBanner.getId()), Emoji.fromFormatted("👍")),
                Button.danger(String.format("banner-bad-%s", gameBanner.getId()), Emoji.fromFormatted("👎"))
        );
    }

    @NotNull
    private EmbedBuilder getEmbedBuilder(Member member, String streamingUrl, String gameName, String streamTitle,
            ResourceBundle resourceBundle) {
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
        return embedBuilder;
    }

    MessageCreateData buildBoringStreamAnnounceMessage(Member member,
            String streamingUrl, String gameName, String streamTitle, Guild guild) {
        log.trace("Building stream announce message for {} server {}", FormatLogObject.memberInfo(member),
                FormatLogObject.guildName(guild));
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        EmbedBuilder embedBuilder = getEmbedBuilder(member, streamingUrl, gameName, streamTitle, resourceBundle);
        embedBuilder.setColor(MessageUtils.getColor(member.getEffectiveAvatarUrl())).setTitle(streamTitle);
        return new MessageCreateBuilder().addEmbeds(embedBuilder.build()).build();
    }
}
