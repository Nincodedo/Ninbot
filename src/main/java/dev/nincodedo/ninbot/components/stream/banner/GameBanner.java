package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.File;

@Data
@Entity
public class GameBanner extends BaseEntity {
    private String gameTitle;
    private int gameId;
    private int logoId;
    private int backgroundId;
    private int score = 0;
    @Transient
    private File file;

    @Transient
    public String getGameTitleStripped() {
        if (gameTitle == null) {
            return null;
        } else {
            return gameTitle.replaceAll("[^A-Za-z0-9]", "");
        }
    }

    @Transient
    public String getFileName() {
        return String.format("%s-%d-%d-%d.png", getGameTitleStripped(), gameId, logoId, backgroundId);
    }
}
