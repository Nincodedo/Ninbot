package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.components.stream.banner.GameBanner;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerBuilder;
import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.message.MessageUtils;
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

    MessageCreateData buildStreamAnnounceMessage(Member member, StreamInstance streamInstance, Guild guild) {
        log.trace("Building stream announce message for {} server {} with game {}", FormatLogObject.memberInfo(member),
                FormatLogObject.guildName(guild), streamInstance.getGame());
        var futureGameBanner = gameBannerBuilder.getGameBannerAsync(streamInstance.getGame());
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        EmbedBuilder embedBuilder = getEmbedBuilder(member, streamInstance, resourceBundle);
        embedBuilder.setColor(MessageUtils.getColor(member.getEffectiveAvatarUrl()))
                .setTitle(streamInstance.getTitle());
        try {
            var gameBanner = futureGameBanner.get();
            if (gameBanner != null) {
                log.trace("Got a game banner! {}", gameBanner);
                embedBuilder.setImage("attachment://" + gameBanner.getFileName());
                if (gameBanner.getScore() == 0) {
                    embedBuilder.setFooter("How did Ninbot do with this generated banner? Leave some feedback with "
                            + "the buttons below!");
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
        log.trace("Game banner didn't work out, building a boring message");
        return buildBoringStreamAnnounceMessage(member, streamInstance, guild);
    }

    private ActionRow getComponentsForGameBanner(GameBanner gameBanner) {
        return ActionRow.of(
                Button.primary(String.format("banner-good-%s", gameBanner.getId()), Emoji.fromFormatted("👍")),
                Button.danger(String.format("banner-bad-%s", gameBanner.getId()), Emoji.fromFormatted("👎"))
        );
    }

    @NotNull
    private EmbedBuilder getEmbedBuilder(Member member, StreamInstance streamInstance, ResourceBundle resourceBundle) {
        EmbedBuilder embedBuilder;
        if (!streamInstance.getUrl().contains("https://")) {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(String.format(resourceBundle.getString("listener.stream.announce.voicechannel"),
                            member.getEffectiveName(), streamInstance.getUrl()), null, member.getEffectiveAvatarUrl())
                    .setTitle(streamInstance.getTitle());
        } else {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(String.format(resourceBundle.getString("listener.stream.announce"),
                            member.getEffectiveName(), streamInstance.getGame(),
                            streamInstance.getUrl()), streamInstance.getUrl(), member.getEffectiveAvatarUrl())
                    .setTitle(streamInstance.getTitle());
        }
        return embedBuilder;
    }

    MessageCreateData buildBoringStreamAnnounceMessage(Member member, StreamInstance streamInstance, Guild guild) {
        log.trace("Building stream announce message for {} server {}", FormatLogObject.memberInfo(member),
                FormatLogObject.guildName(guild));
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        EmbedBuilder embedBuilder = getEmbedBuilder(member, streamInstance, resourceBundle);
        embedBuilder.setColor(MessageUtils.getColor(member.getEffectiveAvatarUrl()))
                .setTitle(streamInstance.getTitle());
        return new MessageCreateBuilder().addEmbeds(embedBuilder.build()).build();
    }
}
