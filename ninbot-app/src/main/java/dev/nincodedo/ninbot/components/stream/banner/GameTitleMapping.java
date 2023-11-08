package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class GameTitleMapping extends BaseEntity {
    @Column(nullable = false)
    private String twitchGameTitle;
    @Column(nullable = false)
    private String bannerGameTitle;
}
