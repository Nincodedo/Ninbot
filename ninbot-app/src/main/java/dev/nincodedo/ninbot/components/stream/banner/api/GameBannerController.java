package dev.nincodedo.ninbot.components.stream.banner.api;

import dev.nincodedo.ninbot.components.stream.banner.GameBannerBuilder;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerRepository;
import dev.nincodedo.nincord.api.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/banners", produces = MediaType.APPLICATION_JSON_VALUE)
public class GameBannerController {

    private GameBannerRepository gameBannerRepository;
    private GameBannerMapper gameBannerMapper;
    private GameBannerBuilder gameBannerBuilder;

    public GameBannerController(GameBannerRepository gameBannerRepository, GameBannerMapper gameBannerMapper,
            GameBannerBuilder gameBannerBuilder) {
        this.gameBannerRepository = gameBannerRepository;
        this.gameBannerMapper = gameBannerMapper;
        this.gameBannerBuilder = gameBannerBuilder;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<GameBannerDTO>> allBanners() {
        var list = gameBannerRepository.findAll()
                .stream()
                .map(gameBannerMapper::mapToDto)
                .sorted(Comparator.comparing(GameBannerDTO::getGameTitle)
                        .reversed()
                        .thenComparing(GameBannerDTO::getScore)
                        .reversed())
                .toList();
        return new ResponseEntity<>(new BaseResponse<>(list), HttpStatus.OK);
    }

    @GetMapping(value = "/cache")
    public ResponseEntity<BaseResponse<GameBannerDTO>> allFromCache() {
        var list = gameBannerBuilder.getGameBannerFilesFromCache("")
                .stream()
                .map(file -> gameBannerBuilder.getGameBannerFromFile(file))
                .flatMap(Optional::stream)
                .map(gameBannerMapper::mapToDto)
                .sorted(Comparator.comparing(GameBannerDTO::getGameTitle)
                        .reversed()
                        .thenComparing(GameBannerDTO::getScore)
                        .reversed())
                .toList();
        return new ResponseEntity<>(new BaseResponse<>(list), HttpStatus.OK);
    }

    @GetMapping(value = "/game/{gameName}")
    public ResponseEntity<BaseResponse<GameBannerDTO>> allByGameName(@PathVariable String gameName) {
        var list = gameBannerRepository.findAllByGameTitle(gameName)
                .stream()
                .map(gameBannerMapper::mapToDto)
                .sorted(Comparator.comparing(GameBannerDTO::getScore).reversed())
                .toList();
        return new ResponseEntity<>(new BaseResponse<>(list), HttpStatus.OK);
    }
}
