package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Data
@Entity
public class NinbotUser extends BaseEntity {
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String serverId;
    private String birthday;
    @Column(nullable = false)
    private Boolean announceBirthday = false;
}
