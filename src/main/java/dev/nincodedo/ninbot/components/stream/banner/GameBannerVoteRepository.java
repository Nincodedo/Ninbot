package dev.nincodedo.ninbot.components.stream.banner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameBannerVoteRepository extends CrudRepository<GameBannerVote, Long> {
}
