package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "game-banner-source", url = "https://www.steamgriddb.com/api/v2", configuration =
        SteamGridDbFeignClientConfiguration.class)
public interface SteamGridDbFeign {
    @GetMapping(value = "/search/autocomplete/{term}")
    BaseResponse<Game> searchGameByName(@PathVariable String term);

    @GetMapping(value = "/heroes/game/{gameId}")
    BaseResponse<GameImage> retrieveHeroByGameId(@PathVariable int gameId);

    @GetMapping(value = "/logos/game/{gameId}")
    BaseResponse<GameImage> retrieveLogoByGameId(@PathVariable int gameId, @RequestParam String[] styles);
}
