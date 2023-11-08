package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import org.springframework.stereotype.Component;

@Component
public class SteamGridDbService {
    private SteamGridDbFeign steamGridDbFeign;

    public SteamGridDbService(SteamGridDbFeign steamGridDbFeign) {
        this.steamGridDbFeign = steamGridDbFeign;
    }

    public SteamGridDbCombinedResponse findImagesFromTitle(String gameTitle) {
        var searchResponse = steamGridDbFeign.searchGameByName(gameTitle);
        if (searchResponse.isSuccess()) {
            var gameId = searchResponse.firstData().id();
            var logoResponse = steamGridDbFeign.retrieveLogoByGameId(gameId, new String[]{"official"});
            filterLockedImages(logoResponse);
            var heroResponse = steamGridDbFeign.retrieveHeroByGameId(gameId);
            filterLockedImages(heroResponse);
            return new SteamGridDbCombinedResponse(searchResponse, logoResponse, heroResponse);
        } else {
            return new SteamGridDbCombinedResponse(searchResponse, null, null);
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
