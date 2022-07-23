package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Entity
public class StreamInstance extends BaseEntity {
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime startTimestamp = LocalDateTime.now();
    private LocalDateTime endTimestamp;
    private String announceMessageId;
    @ManyToOne
    @ToString.Exclude
    private StreamingMember streamingMember;

    public boolean isStreaming() {
        return endTimestamp == null;
    }
}
