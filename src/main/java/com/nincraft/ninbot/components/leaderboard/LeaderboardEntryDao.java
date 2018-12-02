package com.nincraft.ninbot.components.leaderboard;

import com.nincraft.ninbot.components.common.GenericDao;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Log4j2
@Repository
@Transactional
public class LeaderboardEntryDao extends GenericDao<LeaderboardEntry> {
    public LeaderboardEntryDao(EntityManager entityManager) {
        super(entityManager);
    }

    public void recordWin(LeaderboardEntry leaderboardEntry) {
        log.debug("Recording win on server {} for user {}", leaderboardEntry.getServerId(), leaderboardEntry.getUserId());
        leaderboardEntry.setWins(leaderboardEntry.getWins() + 1);
        entityManager.merge(leaderboardEntry);
        entityManager.flush();
    }

    public void recordLoss(LeaderboardEntry leaderboardEntry) {
        log.debug("Recording loss on server {} for user {}", leaderboardEntry.getServerId(), leaderboardEntry.getUserId());
        leaderboardEntry.setLoses(leaderboardEntry.getLoses() + 1);
        entityManager.merge(leaderboardEntry);
        entityManager.flush();
    }

    public void recordTie(LeaderboardEntry leaderboardEntry) {
        log.debug("Recording tie on server {} for user {}", leaderboardEntry.getServerId(), leaderboardEntry.getUserId());
        leaderboardEntry.setTies(leaderboardEntry.getTies() + 1);
        entityManager.merge(leaderboardEntry);
        entityManager.flush();
    }

    public LeaderboardEntry getUserEntry(String serverId, String userId) {
        log.debug("Getting leaderboard entry on server {} for user {}", serverId, userId);
        val query = entityManager.createQuery("FROM LeaderboardEntry where serverId = :serverId and userId = :userId", LeaderboardEntry.class);
        query.setParameter("serverId", serverId);
        query.setParameter("userId", userId);
        val results = query.getResultList();
        if (results.size() == 1) {
            return results.get(0);
        }
        return new LeaderboardEntry(serverId, userId);
    }
}
