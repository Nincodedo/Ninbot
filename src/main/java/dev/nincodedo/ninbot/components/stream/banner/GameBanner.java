package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.io.File;
import java.util.List;

@Data
@Entity
public class GameBanner extends BaseEntity {
    private String gameTitle;
    private int gameId;
    private int logoId;
    private int backgroundId;
    @Transient
    private int score = 0;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "gameBanner",
            fetch = FetchType.EAGER
    )
    private List<GameBannerVote> votes;
    @Transient
    private File file;

    @PostLoad
    private void onLoad() {
        this.score = votes.stream()
                .filter(gameBannerVote -> gameBannerVote.getUserId() != null)
                .map(GameBannerVote::getVote)
                .reduce(0, Integer::sum);
    }

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
