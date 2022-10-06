package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

public record CombinedResponse(BaseResponse<GameImage> logoResponse,
                               BaseResponse<GameImage> heroResponse) {
}
