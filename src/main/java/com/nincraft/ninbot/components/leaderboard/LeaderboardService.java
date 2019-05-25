package com.nincraft.ninbot.components.leaderboard;

import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private LeaderboardEntryDao leaderboardEntryDao;

    public LeaderboardService(LeaderboardEntryDao leaderboardEntryDao) {
        this.leaderboardEntryDao = leaderboardEntryDao;
    }

    List<LeaderboardEntry> getAllEntriesForServer(String serverId) {
        return leaderboardEntryDao.getAllObjectsByServerId(serverId);
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
        val winnerEntry = leaderboardEntryDao.getUserEntry(serverId, winner);
        leaderboardEntryDao.recordWin(winnerEntry);

        val loserEntry = leaderboardEntryDao.getUserEntry(serverId, loser);
        leaderboardEntryDao.recordLoss(loserEntry);
    }

    void removeAllEntriesForServer(String serverId) {
        leaderboardEntryDao.removeAllEntriesForServer(serverId);
    }
}
