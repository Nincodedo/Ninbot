package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class SteamGridDbServiceTest {

    @Mock
    SteamGridDbFeign steamGridDbFeign;
    @InjectMocks
    SteamGridDbService steamGridDbService;

    @Test
    void findImagesFromTitle() throws MalformedURLException {
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

        when(steamGridDbFeign.searchGameByName("Zeldo")).thenReturn(gameBaseResponse);
        when(steamGridDbFeign.retrieveLogoByGameId(gameBaseResponse.firstData()
                .id(), new String[]{"official"})).thenReturn(logoResponse);
        when(steamGridDbFeign.retrieveHeroByGameId(gameBaseResponse.firstData().id())).thenReturn(heroResponse);

        var combinedResponse = steamGridDbService.findImagesFromTitle("Zeldo");

        assertThat(combinedResponse.allResponsesSuccessful()).isTrue();
    }

    @Test
    void findImagesFromTitleButOopsAllLocked() throws MalformedURLException {
        var bg = Path.of("..", "docs", "images", "ninbot-github-social.png").toUri().toURL();
        var icon = Path.of("..", "docs", "images", "ninbot-github-logo-small.png").toUri().toURL();
        BaseResponse<Game> gameBaseResponse = Instancio.of(new TypeToken<BaseResponse<Game>>() {
        }).set(field("success"), true).create();
        List<GameImage> backgrounds = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + bg.getPath()).set(field("lock"), true).stream().limit(5).toList();
        List<GameImage> logos = Instancio.of(new TypeToken<GameImage>() {
        }).set(field("url"), "file://" + icon.getPath()).set(field("lock"), true).stream().limit(5).toList();
        BaseResponse<GameImage> logoResponse = Instancio.of(new TypeToken<BaseResponse<GameImage>>() {
        }).set(field("success"), true).set(field("data"), logos).create();
        BaseResponse<GameImage> heroResponse = Instancio.of(new TypeToken<BaseResponse<GameImage>>() {
        }).set(field("success"), true).set(field("data"), backgrounds).create();

        when(steamGridDbFeign.searchGameByName("Zeldo")).thenReturn(gameBaseResponse);
        when(steamGridDbFeign.retrieveLogoByGameId(gameBaseResponse.firstData()
                .id(), new String[]{"official"})).thenReturn(logoResponse);
        when(steamGridDbFeign.retrieveHeroByGameId(gameBaseResponse.firstData().id())).thenReturn(heroResponse);

        var combinedResponse = steamGridDbService.findImagesFromTitle("Zeldo");

        assertThat(combinedResponse.allResponsesSuccessful()).isFalse();
    }

    @Test
    void findImagesFromTitleThatDoesntExist() {
        BaseResponse<Game> gameBaseResponse = Instancio.of(new TypeToken<BaseResponse<Game>>() {
        }).set(field("success"), false).create();

        when(steamGridDbFeign.searchGameByName("Zeldo")).thenReturn(gameBaseResponse);

        var combinedResponse = steamGridDbService.findImagesFromTitle("Zeldo");

        assertThat(combinedResponse.allResponsesSuccessful()).isFalse();
        assertThat(combinedResponse.search().isSuccess()).isFalse();
    }
}
