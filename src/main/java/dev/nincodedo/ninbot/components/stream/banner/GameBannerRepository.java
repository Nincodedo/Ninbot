package dev.nincodedo.ninbot.components.stream.banner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameBannerRepository extends CrudRepository<GameBanner, Long> {
    List<GameBanner> findAllByGameTitle(String gameTitle);
    Optional<GameBanner> findGameBannerByLogoIdAndBackgroundId(int logoId, int backgroundId);
}
