package dev.nincodedo.ninbot.components.stream.banner.api;

import dev.nincodedo.ninbot.components.stream.banner.GameBanner;
import org.mapstruct.Mapper;

@Mapper
public interface GameBannerMapper {
    GameBannerDTO mapToDto(GameBanner gameBanner);
}
