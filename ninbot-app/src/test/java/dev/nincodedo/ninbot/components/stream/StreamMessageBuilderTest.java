package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.components.stream.banner.GameBanner;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerBuilder;
import dev.nincodedo.nincord.config.db.Config;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamMessageBuilderTest {
    @Mock
    GameBannerBuilder gameBannerBuilder;
    @Mock
    ConfigService configService;
    @InjectMocks
    StreamMessageBuilder streamMessageBuilder;

    @Test
    void buildWithBanner() {
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        StreamInstance streamInstance = new StreamInstance();
        streamInstance.setGame("Zeldo");
        streamInstance.setUrl("https://twitch.tv/nincodedo");
        GameBanner gameBanner = new GameBanner();
        gameBanner.setFile(new File("../docs/images/ninbot-github-social.png"));
        CompletableFuture<GameBanner> completedFuture = CompletableFuture.completedFuture(gameBanner);

        when(guild.getLocale()).thenReturn(DiscordLocale.ENGLISH_US);
        when(gameBannerBuilder.getGameBannerAsync("Zeldo")).thenReturn(completedFuture);

        var message = streamMessageBuilder.buildStreamAnnounceMessage(member, streamInstance, guild);

        assertThat(message).isNotNull();
        assertThat(message.getFiles()).isNotEmpty();
        assertThat(message.getEmbeds()).isNotEmpty();
        assertThat(message.getEmbeds().get(0)).isNotNull();
        assertThat(message.getEmbeds().get(0).getFooter()).isNotNull();
        assertThat(message.getEmbeds().get(0).getFooter().getText()).contains("generated");
    }

    @Test
    void buildWithBannerNoFooter() {
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        StreamInstance streamInstance = new StreamInstance();
        streamInstance.setGame("Zeldo");
        streamInstance.setUrl("https://twitch.tv/nincodedo");
        GameBanner gameBanner = new GameBanner();
        gameBanner.setFile(new File("../docs/images/ninbot-github-social.png"));
        gameBanner.setScore(1);
        CompletableFuture<GameBanner> completedFuture = CompletableFuture.completedFuture(gameBanner);

        when(guild.getLocale()).thenReturn(DiscordLocale.ENGLISH_US);
        when(gameBannerBuilder.getGameBannerAsync("Zeldo")).thenReturn(completedFuture);

        var message = streamMessageBuilder.buildStreamAnnounceMessage(member, streamInstance, guild);

        assertThat(message).isNotNull();
        assertThat(message.getFiles()).isNotEmpty();
        assertThat(message.getEmbeds()).isNotEmpty();
        assertThat(message.getEmbeds().get(0)).isNotNull();
        assertThat(message.getEmbeds().get(0).getFooter()).isNull();
    }

    @Test
    void buildWithoutBannerTitleOnDenyList() {
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        StreamInstance streamInstance = new StreamInstance();
        streamInstance.setGame("Zeldo");
        streamInstance.setUrl("https://twitch.tv/nincodedo");
        GameBanner gameBanner = new GameBanner();
        gameBanner.setFile(new File("../docs/images/ninbot-github-social.png"));
        List<Config> configs = new ArrayList<>();
        configs.add(new Config("1", ConfigConstants.STREAMING_GAME_TITLE_BANNER_DENY_LIST, "Zeldo"));

        when(configService.getGlobalConfigsByName(ConfigConstants.STREAMING_GAME_TITLE_BANNER_DENY_LIST, "1")).thenReturn(configs);
        when(guild.getId()).thenReturn("1");
        when(guild.getLocale()).thenReturn(DiscordLocale.ENGLISH_US);

        var message = streamMessageBuilder.buildStreamAnnounceMessage(member, streamInstance, guild);

        assertThat(message).isNotNull();
        assertThat(message.getFiles()).isEmpty();
        assertThat(message.getEmbeds()).isNotEmpty();
        assertThat(message.getEmbeds().get(0)).isNotNull();
        assertThat(message.getEmbeds().get(0).getFooter()).isNull();
    }
}
