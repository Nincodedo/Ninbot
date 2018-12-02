package com.nincraft.ninbot.components.leaderboard;

import org.springframework.stereotype.Component;

@Component
public class LoseCommand extends WinCommand {
    public LoseCommand(LeaderboardService leaderboardService) {
        super(leaderboardService);
        name = "lose";
    }
}
