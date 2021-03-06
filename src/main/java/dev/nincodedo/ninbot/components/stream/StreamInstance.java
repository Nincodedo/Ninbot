package dev.nincodedo.ninbot.components.stream;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class StreamInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
