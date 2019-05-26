package com.nincraft.ninbot.components.leaderboard;

import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private LeaderboardEntryRepository leaderboardEntryRepository;

    public LeaderboardService(LeaderboardEntryRepository leaderboardEntryRepository) {
        this.leaderboardEntryRepository = leaderboardEntryRepository;
    }

    List<LeaderboardEntry> getAllEntriesForServer(String serverId) {
        return leaderboardEntryRepository.getAllByServerId(serverId);
    }

    void recordResult(String serverId, String recordType, String firstUser, String againstUser) {
        String winner;
        String loser;
        if ("win".equals(recordType)) {
            winner = firstUser;
            loser = againstUser;
        } else {
            winner = againstUser;
            loser = firstUser;
        }
        val winnerEntry = leaderboardEntryRepository.
                getFirstByServerIdAndUserId(serverId, winner).orElseGet(() -> new LeaderboardEntry(serverId, winner));

        winnerEntry.setWins(winnerEntry.getWins() + 1);
        leaderboardEntryRepository.save(winnerEntry);

        val loserEntry = leaderboardEntryRepository.
                getFirstByServerIdAndUserId(serverId, loser).orElseGet(() -> new LeaderboardEntry(serverId, loser));
        loserEntry.setLoses(loserEntry.getLoses() + 1);
        leaderboardEntryRepository.save(loserEntry);
    }

    void removeAllEntriesForServer(String serverId) {
        leaderboardEntryRepository.deleteAllByServerId(serverId);
    }
}
