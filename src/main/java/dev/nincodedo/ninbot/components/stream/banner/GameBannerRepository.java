package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameBannerRepository extends BaseRepository<GameBanner> {
    default List<GameBanner> findAllByGameTitle(String gameTitle) {
        return findAllByGameTitleAndDeleted(gameTitle, false);
    }

    default Optional<GameBanner> findGameBannerByLogoIdAndBackgroundId(int logoId, int backgroundId) {
        return findGameBannerByLogoIdAndBackgroundIdAndDeleted(logoId, backgroundId, false);
    }

    List<GameBanner> findAllByGameTitleAndDeleted(String gameTitle, Boolean isDeleted);

    Optional<GameBanner> findGameBannerByLogoIdAndBackgroundIdAndDeleted(int logoId, int backgroundId,
            Boolean isDeleted);
}
