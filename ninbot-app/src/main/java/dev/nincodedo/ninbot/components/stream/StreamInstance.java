package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class StreamInstance extends BaseEntity {
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime startTimestamp = LocalDateTime.now();
    private LocalDateTime endTimestamp;
    private String announceMessageId;
    private String url;
    private String game;
    private String title;
    @ManyToOne
    @ToString.Exclude
    private StreamingMember streamingMember;

    public boolean isStreaming() {
        return endTimestamp == null;
    }
}
