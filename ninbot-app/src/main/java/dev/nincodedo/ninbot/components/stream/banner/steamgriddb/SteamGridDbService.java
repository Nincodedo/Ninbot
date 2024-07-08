package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SteamGridDbService {
    private SteamGridDbFeign steamGridDbFeign;

    public SteamGridDbService(SteamGridDbFeign steamGridDbFeign) {
        this.steamGridDbFeign = steamGridDbFeign;
    }

    public SteamGridDbCombinedResponse findImagesFromTitle(String gameTitle) {
        var searchResponse = steamGridDbFeign.searchGameByName(gameTitle);
        log.trace("Searched for game title {}, success: {}", gameTitle, searchResponse.isSuccess());
        if (searchResponse.isSuccess()) {
            var gameId = searchResponse.firstData().id();
            log.trace("Found id {} for game title {}", gameId, gameTitle);
            var logoResponse = steamGridDbFeign.retrieveLogoByGameId(gameId, new String[]{"official"});
            filterUnwantedImages(logoResponse);
            var heroResponse = steamGridDbFeign.retrieveHeroByGameId(gameId);
            filterUnwantedImages(heroResponse);
            return new SteamGridDbCombinedResponse(searchResponse, logoResponse, heroResponse);
        } else {
            return new SteamGridDbCombinedResponse(searchResponse, null, null);
        }
    }

    private void filterUnwantedImages(BaseResponse<GameImage> gameImageBaseResponse) {
        var countBefore = gameImageBaseResponse.getData().size();
        gameImageBaseResponse.setData(gameImageBaseResponse.getData()
                .stream()
                .filter(gameImage -> !gameImage.lock())
                .filter(gameImage -> "en".equalsIgnoreCase(gameImage.language()))
                .toList());
        var countAfter = gameImageBaseResponse.getData().size();
        log.trace("Filtering unwanted images, before {} after {}", countBefore, countAfter);
        if (gameImageBaseResponse.isSuccess()) {
            gameImageBaseResponse.setSuccess(!gameImageBaseResponse.getData().isEmpty());
        }
    }
}
