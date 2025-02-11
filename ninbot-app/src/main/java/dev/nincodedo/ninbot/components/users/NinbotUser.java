package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class NinbotUser extends BaseEntity {
    @Column(nullable = false)
    private String userId;
}
