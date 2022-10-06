package dev.nincodedo.ninbot.components.stream.banner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameBannerVoteRepository extends CrudRepository<GameBannerVote, Long> {
    Optional<GameBannerVote> findGameBannerVoteByGameBanner_IdAndUserId(Long gameBannerId, String userId);
}
