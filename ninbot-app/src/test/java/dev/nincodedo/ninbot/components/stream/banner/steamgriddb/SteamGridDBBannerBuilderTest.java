package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import dev.nincodedo.ninbot.components.stream.banner.GameBanner;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerRepository;
import org.apache.commons.io.FileUtils;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class SteamGridDBBannerBuilderTest {

    static File cache = new File("cache");
    @Mock
    SteamGridDBFeign steamGridDBFeign;
    @Mock
    GameBannerRepository gameBannerRepository;
    @InjectMocks
    SteamGridDBBannerBuilder steamGridDBBannerBuilder;

    @BeforeAll
    static void before() throws IOException {
        if (cache.exists() && !cache.isDirectory()) {
            if (!cache.delete()) {
                fail("Failed to delete non directory cache file");
            }
        }
        if (!cache.mkdir()) {
            fail("Failed to make cache directory");
        }
        FileUtils.cleanDirectory(cache);
    }

    @AfterAll
    static void after() throws IOException {
        FileUtils.deleteDirectory(cache);
    }

    @Test
    void generateGameBannerFromTitle() throws MalformedURLException, ExecutionException, InterruptedException {
        var bg = Path.of("..", "docs", "images", "ninbot-github-social.png").toUri().toURL();
        var icon = Path.of("..", "docs", "images", "ninbot-github-logo-small.png").toUri().toURL();
        BaseResponse<Game> gameBaseResponse = Instancio.of(new TypeToken<BaseResponse<Game>>() {
        }).set(field("success"), true).create();
        List<GameImage> backgrounds = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + bg.getPath()).set(field("lock"), false).stream().limit(5).toList();
        List<GameImage> logos = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + icon.getPath()).set(field("lock"), false).stream().limit(5).toList();
        BaseResponse<GameImage> logoResponse = Instancio.of(new TypeToken<BaseResponse<GameImage>>() {
        }).set(field("success"), true).set(field("data"), logos).create();
        BaseResponse<GameImage> heroResponse = Instancio.of(new TypeToken<BaseResponse<GameImage>>() {
        }).set(field("success"), true).set(field("data"), backgrounds).create();

        var combinedResponse = new SteamGridDBCombinedResponse(gameBaseResponse, logoResponse, heroResponse);

        when(steamGridDBFeign.findImagesFromTitle("Zeldo")).thenReturn(combinedResponse);

        var bannerAsync = steamGridDBBannerBuilder.getGameBannerAsync("Zeldo");
        var banner = bannerAsync.get();

        assertThat(banner.getGameTitle()).isEqualTo("Zeldo");
        assertThat(cache).isNotEmptyDirectory();
        assertThat(cache.listFiles()).hasSizeBetween(1, 3);
        String bannerFileName = cache.listFiles()[0].getName();
        assertThat(bannerFileName).contains("Zeldo");
        assertThat(banner.getFile()).exists();

        banner.setFile(null);
        when(gameBannerRepository.findGameBannerByLogoIdAndBackgroundId(anyInt(), anyInt())).thenReturn(Optional.of(banner));

        verify(steamGridDBFeign).findImagesFromTitle("Zeldo");

        var cachedBannerAsync = steamGridDBBannerBuilder.getGameBannerAsync("Zeldo");
        var cachedBanner = cachedBannerAsync.get();

        assertThat(cachedBanner.getFile().getName()).contains("Zeldo");

        var bannerFromFileOptional = steamGridDBBannerBuilder.getGameBannerFromFile(cachedBanner.getFile());

        assertThat(bannerFromFileOptional).isPresent().contains(banner);

        verifyNoMoreInteractions(steamGridDBFeign);
    }

    @Test
    void generateGameBannerFromTitleWithPoorScores() throws MalformedURLException, ExecutionException,
            InterruptedException {
        var bg = Path.of("..", "docs", "images", "ninbot-github-social.png").toUri().toURL();
        var icon = Path.of("..", "docs", "images", "ninbot-github-logo-small.png").toUri().toURL();
        BaseResponse<Game> gameBaseResponse = Instancio.of(new TypeToken<BaseResponse<Game>>() {
        }).set(field("success"), true).create();
        List<GameImage> backgrounds = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + bg.getPath()).stream().limit(5).toList();
        List<GameImage> logos = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + icon.getPath()).stream().limit(5).toList();
        BaseResponse<GameImage> logoResponse = Instancio.of(new TypeToken<BaseResponse<GameImage>>() {
        }).set(field("success"), true).set(field("data"), logos).create();
        BaseResponse<GameImage> heroResponse = Instancio.of(new TypeToken<BaseResponse<GameImage>>() {
        }).set(field("success"), true).set(field("data"), backgrounds).create();

        List<GameBanner> poorScoreBanners = new ArrayList<>();
        for (var logo : logos) {
            for (var background : backgrounds) {
                var gameBanner = Instancio.of(new TypeToken<GameBanner>() {
                        })
                        .set(field("logoId"), logo.id())
                        .set(field("backgroundId"), background.id())
                        .set(field("score"), -5)
                        .create();
                poorScoreBanners.add(gameBanner);
            }
        }

        var combinedResponse = new SteamGridDBCombinedResponse(gameBaseResponse, logoResponse, heroResponse);

        when(steamGridDBFeign.findImagesFromTitle("Zeldo")).thenReturn(combinedResponse);
        when(gameBannerRepository.findAllByGameTitle("Zeldo")).thenReturn(poorScoreBanners);

        var bannerAsync = steamGridDBBannerBuilder.getGameBannerAsync("Zeldo");
        var banner = bannerAsync.get();

        assertThat(banner).isNull();
        assertThat(cache).isEmptyDirectory();
    }
}
