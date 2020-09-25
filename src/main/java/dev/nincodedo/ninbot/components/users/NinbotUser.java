package dev.nincodedo.ninbot.components.users;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class NinbotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String userId;
    private String serverId;
    private String birthday;
}
