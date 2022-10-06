package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Data
@Entity
public class GameBannerVote extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private GameBanner gameBanner;
    private String userId;
    private Integer vote;
}
