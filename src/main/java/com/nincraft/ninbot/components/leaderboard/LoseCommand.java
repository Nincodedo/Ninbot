package com.nincraft.ninbot.components.leaderboard;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class LoseCommand extends WinCommand {
    public LoseCommand(LeaderboardService leaderboardService) {
        super(leaderboardService);
        name = "lose";
        helpText = "lose @Username";
        aliases = Arrays.asList("lost", "loss");
    }
}
