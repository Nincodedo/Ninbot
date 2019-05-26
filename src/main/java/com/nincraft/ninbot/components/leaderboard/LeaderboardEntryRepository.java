package com.nincraft.ninbot.components.leaderboard;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LeaderboardEntryRepository extends CrudRepository<LeaderboardEntry, Long> {
    List<LeaderboardEntry> getAllByServerId(String serverId);

    Optional<LeaderboardEntry> getFirstByServerIdAndUserId(String serverId, String userId);

    void deleteAllByServerId(String serverId);
}
