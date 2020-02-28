package com.nincraft.ninbot.components.users;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class NinbotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String userId;
    private String serverId;
    private Date birthday;
}
