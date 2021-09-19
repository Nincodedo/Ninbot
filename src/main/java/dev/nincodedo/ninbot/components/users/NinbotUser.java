package dev.nincodedo.ninbot.components.users;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class NinbotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String serverId;
    private String birthday;
    @Column(nullable = false)
    private Boolean announceBirthday = false;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @PreUpdate
    private void updateModified() {
        modifiedAt = LocalDateTime.now();
    }
}
