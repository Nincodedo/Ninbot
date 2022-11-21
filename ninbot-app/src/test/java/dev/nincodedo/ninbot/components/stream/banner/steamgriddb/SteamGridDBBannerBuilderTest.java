package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import dev.nincodedo.ninbot.NinbotApplication;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
@ContextConfiguration(classes = {NinbotApplication.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
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
        }).set(field("url"), "file://" + bg.getPath()).stream().limit(5).toList();
        List<GameImage> logos = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + icon.getPath()).stream().limit(5).toList();
        BaseResponse<GameImage> logoResponse = Instancio.of(new TypeToken<BaseResponse<GameImage>>() {
        }).set(field("success"), true).set(field("data"), logos).create();
        BaseResponse<GameImage> heroResponse = Instancio.of(new TypeToken<BaseResponse<GameImage>>() {
        }).set(field("success"), true).set(field("data"), backgrounds).create();

        when(steamGridDBFeign.searchGameByName(anyString())).thenReturn(gameBaseResponse);
        when(steamGridDBFeign.retrieveLogoByGameId(gameBaseResponse.firstData()
                .id(), new String[]{"official"})).thenReturn(logoResponse);
        when(steamGridDBFeign.retrieveHeroByGameId(gameBaseResponse.firstData().id())).thenReturn(heroResponse);

        var bannerAsync = steamGridDBBannerBuilder.getGameBannerAsync("Zeldo");
        var banner = bannerAsync.get();

        assertThat(banner.getGameTitle()).isEqualTo("Zeldo");
        assertThat(cache).isNotEmptyDirectory();
        String bannerFileName = cache.listFiles()[0].getName();
        assertThat(bannerFileName).contains("Zeldo");
        assertThat(banner.getFile()).exists();

        banner.setFile(null);
        when(gameBannerRepository.findGameBannerByLogoIdAndBackgroundId(anyInt(), anyInt()))
                .thenReturn(Optional.of(banner));

        verify(steamGridDBFeign).searchGameByName(anyString());
        verify(steamGridDBFeign).retrieveHeroByGameId(gameBaseResponse.firstData().id());
        verify(steamGridDBFeign).retrieveLogoByGameId(gameBaseResponse.firstData().id(), new String[]{"official"});

        var cachedBannerAsync = steamGridDBBannerBuilder.getGameBannerAsync("Zeldo");
        var cachedBanner = cachedBannerAsync.get();

        assertThat(cachedBanner.getFile().getName()).contains("Zeldo");

        var bannerFromFileOptional = steamGridDBBannerBuilder.getGameBannerFromFile(cachedBanner.getFile());

        assertThat(bannerFromFileOptional).isPresent().contains(banner);

        verifyNoMoreInteractions(steamGridDBFeign);
    }
}
