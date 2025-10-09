package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.components.stream.banner.GameBanner;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerBuilder;
import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.config.db.Config;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.message.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class StreamMessageBuilder {

    private GameBannerBuilder gameBannerBuilder;
    private ConfigService configService;

    StreamMessageBuilder(GameBannerBuilder gameBannerBuilder, ConfigService configService) {
        this.gameBannerBuilder = gameBannerBuilder;
        this.configService = configService;
    }

    MessageCreateData buildStreamAnnounceMessage(Member member, StreamInstance streamInstance, Guild guild) {
        log.trace("Building stream announce message for {} server {} with game {}", FormatLogObject.memberInfo(member),
                FormatLogObject.guildName(guild), streamInstance.getGame());
        var configs =
                configService.getGlobalConfigsByName(ConfigConstants.STREAMING_GAME_TITLE_BANNER_DENY_LIST,
                        guild.getId());
        var isGameOnBannerDenyList = configs.stream()
                .map(Config::getValue)
                .anyMatch(value -> streamInstance.getGame().equalsIgnoreCase(value));
        MessageCreateData announceMessage = null;
        if (!isGameOnBannerDenyList) {
            announceMessage = buildStreamAnnounceMessageWithBanner(member, streamInstance, guild);
        } else {
            log.trace("Game {} was on the deny list, skipping banner creation.", streamInstance.getGame());
        }
        if (announceMessage == null) {
            log.trace("Game banner didn't work out, building a boring message");
            announceMessage = buildBoringStreamAnnounceMessage(member, streamInstance, guild);
        }
        return announceMessage;
    }

    @Nullable
    private MessageCreateData buildStreamAnnounceMessageWithBanner(Member member, StreamInstance streamInstance,
            Guild guild) {
        var futureGameBanner = gameBannerBuilder.getGameBannerAsync(streamInstance.getGame());
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        EmbedBuilder embedBuilder = getEmbedBuilder(member, streamInstance, resourceBundle);
        try {
            var gameBanner = futureGameBanner.get();
            if (gameBanner != null) {
                log.trace("Got a game banner! {}", gameBanner);
                embedBuilder.setImage("attachment://" + gameBanner.getFileName());
                if (gameBanner.getScore() == 0) {
                    embedBuilder.setFooter(resourceBundle.getString("listener.stream.announce.banner.footer"));
                }
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
        return null;
    }

    private ActionRow getComponentsForGameBanner(GameBanner gameBanner) {
        return ActionRow.of(
                Button.primary("banner-good-%s".formatted(gameBanner.getId()), Emoji.fromFormatted("👍")),
                Button.danger("banner-bad-%s".formatted(gameBanner.getId()), Emoji.fromFormatted("👎"))
        );
    }

    @NotNull
    private EmbedBuilder getEmbedBuilder(Member member, StreamInstance streamInstance, ResourceBundle resourceBundle) {
        EmbedBuilder embedBuilder;
        if (!streamInstance.getUrl().contains("https://")) {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(resourceBundle.getString("listener.stream.announce.voicechannel")
                                    .formatted(member.getEffectiveName(), streamInstance.getUrl()), null,
                            member.getEffectiveAvatarUrl())
                    .setTitle(streamInstance.getTitle());
        } else {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(resourceBundle.getString("listener.stream.announce")
                                    .formatted(member.getEffectiveName(), streamInstance.getGame(),
                                            streamInstance.getUrl()),
                            streamInstance.getUrl(), member.getEffectiveAvatarUrl())
                    .setTitle(streamInstance.getTitle());
        }
        return embedBuilder.setColor(MessageUtils.getColor(member.getEffectiveAvatarUrl()))
                .setTitle(streamInstance.getTitle());
    }

    MessageCreateData buildBoringStreamAnnounceMessage(Member member, StreamInstance streamInstance, Guild guild) {
        log.trace("Building stream announce message for {} server {}", FormatLogObject.memberInfo(member),
                FormatLogObject.guildName(guild));
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        EmbedBuilder embedBuilder = getEmbedBuilder(member, streamInstance, resourceBundle);
        return new MessageCreateBuilder().addEmbeds(embedBuilder.build()).build();
    }
}
