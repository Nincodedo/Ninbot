package dev.nincodedo.ninbot.components.pathogen.user;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Date;

@Entity
@Data
public class PathogenUser extends BaseEntity {

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String serverId;
    private Integer infectionLevel = 0;
    private Date lastInitialInfection;
    private Date lastInitialCure;
    private Boolean vaccinated = false;
}
