package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.nincord.config.db.component.ComponentConfiguration;
import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
public class NinbotUser extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String userId;
    @Transient
    private List<ComponentConfiguration> userSettings = new ArrayList<>();

    public NinbotUser(String userId) {
        this.userId = userId;
    }
}
