package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

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
