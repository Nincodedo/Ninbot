package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

public record SteamGridDbCombinedResponse(BaseResponse<Game> search, BaseResponse<GameImage> logos,
                                          BaseResponse<GameImage> heroes) {
    public boolean allResponsesSuccessful() {
        return search.isSuccess() && logos.isSuccess() && heroes.isSuccess() && !logos.getData().isEmpty()
                && !heroes.getData().isEmpty();
    }

    public int getGameId() {
        return search.firstData().id();
    }
}
