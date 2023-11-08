package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.nincord.persistence.BaseRepository;

public interface GameTitleMappingRepository extends BaseRepository<GameTitleMapping> {
    GameTitleMapping findByTwitchGameTitle(String twitchGameTitle);
}
