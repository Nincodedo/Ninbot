package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class GameBannerVote extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private GameBanner gameBanner;
    private Integer vote;
}
