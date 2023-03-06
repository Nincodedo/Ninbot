package dev.nincodedo.ninbot.components.stream.banner.api;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameBannerDTO {
    private String gameTitle;
    private int gameId;
    private int logoId;
    private int backgroundId;
    private int score;
    private int uses;
    private LocalDateTime lastUse;
}
