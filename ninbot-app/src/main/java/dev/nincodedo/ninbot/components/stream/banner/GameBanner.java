package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.io.File;
import java.util.ArrayList;
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
    private List<GameBannerVote> votes = new ArrayList<>();
    @Transient
    private File file;

    @Override
    protected void postLoad() {
        super.postLoad();
        this.score = votes.stream()
                .filter(gameBannerVote -> gameBannerVote.getAudit().getCreatedBy() != null)
                .map(GameBannerVote::getVote)
                .reduce(0, Integer::sum);
    }

    @Transient
    public String getFileName() {
        return String.format("%s-%d-%d-%d.png", GameBannerUtils.getCachedFileName(gameTitle), gameId, logoId,
                backgroundId);
    }
}
