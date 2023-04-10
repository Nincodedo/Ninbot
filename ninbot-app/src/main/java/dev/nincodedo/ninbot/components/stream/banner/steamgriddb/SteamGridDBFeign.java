package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "game-banner-source", url = "https://www.steamgriddb.com/api/v2", configuration =
        SteamGridDbFeignClientConfiguration.class)
public interface SteamGridDBFeign {
    @GetMapping(value = "/search/autocomplete/{term}")
    BaseResponse<Game> searchGameByName(@PathVariable String term);

    @GetMapping(value = "/heroes/game/{gameId}")
    BaseResponse<GameImage> retrieveHeroByGameId(@PathVariable int gameId);

    @GetMapping(value = "/logos/game/{gameId}")
    BaseResponse<GameImage> retrieveLogoByGameId(@PathVariable int gameId, @RequestParam String[] styles);

    default SteamGridDBCombinedResponse findImagesFromTitle(String gameTitle) {
        var searchResponse = searchGameByName(gameTitle);
        if (searchResponse.isSuccess()) {
            var gameId = searchResponse.firstData().id();
            var logoResponse = retrieveLogoByGameId(gameId, new String[]{"official"});
            filterLockedImages(logoResponse);
            var heroResponse = retrieveHeroByGameId(gameId);
            filterLockedImages(heroResponse);
            return new SteamGridDBCombinedResponse(searchResponse, logoResponse, heroResponse);
        } else {
            return new SteamGridDBCombinedResponse(searchResponse, null, null);
        }
    }

    private void filterLockedImages(BaseResponse<GameImage> gameImageBaseResponse) {
        gameImageBaseResponse.setData(gameImageBaseResponse.getData()
                .stream()
                .filter(gameImage -> !gameImage.lock())
                .toList());
        if (gameImageBaseResponse.isSuccess()) {
            gameImageBaseResponse.setSuccess(!gameImageBaseResponse.getData().isEmpty());
        }
    }
}
