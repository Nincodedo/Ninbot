package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import dev.nincodedo.ninbot.NinbotRunner;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class SteamGridDBBannerBuilderTest {

    @Mock
    SteamGridDBFeign steamGridDBFeign;

    @Mock
    GameBannerRepository gameBannerRepository;

    @InjectMocks
    SteamGridDBBannerBuilder steamGridDBBannerBuilder;

    static File cache = new File("cache");

    @BeforeAll
    static void before() throws IOException {
        cache.mkdir();
        FileUtils.cleanDirectory(cache);
    }

    @AfterAll
    static void after() throws IOException {
        FileUtils.deleteDirectory(cache);
    }

    @Test
    void generateGameBannerFromTitle() throws MalformedURLException, ExecutionException, InterruptedException {
        var bg = Path.of("docs", "images", "ninbot-github-social.png").toUri().toURL();
        var icon = Path.of("docs", "images", "ninbot-github-logo-small.png").toUri().toURL();
        BaseResponse<Game> gameBaseResponse = Instancio.of(new TypeToken<BaseResponse<Game>>() {
        }).set(field("success"), true).create();
        GameImage background = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + bg.getPath()).create();
        GameImage logo = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + icon.getPath()).create();
        List<GameImage> backgrounds = new ArrayList<>();
        backgrounds.add(background);
        List<GameImage> logos = new ArrayList<>();
        logos.add(logo);
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

        banner.setFile(null);
        when(gameBannerRepository.findGameBannerByLogoIdAndBackgroundId(banner.getLogoId(), banner.getBackgroundId()))
                .thenReturn(Optional.of(banner));

        verify(steamGridDBFeign).searchGameByName(anyString());
        verify(steamGridDBFeign).retrieveHeroByGameId(gameBaseResponse.firstData().id());
        verify(steamGridDBFeign).retrieveLogoByGameId(gameBaseResponse.firstData().id(), new String[]{"official"});

        var cachedBannerAsync = steamGridDBBannerBuilder.getGameBannerAsync("Zeldo");
        var cachedBanner = cachedBannerAsync.get();

        assertThat(cachedBanner.getFile()).hasName(bannerFileName);

        verifyNoMoreInteractions(steamGridDBFeign);
    }
}
