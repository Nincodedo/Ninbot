package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameBannerRepository extends BaseRepository<GameBanner> {

    Optional<GameBanner> findGameBannerByLogoIdAndBackgroundId(int logoId, int backgroundId);
    List<GameBanner> findAllByGameTitle(String gameTitle);
}
