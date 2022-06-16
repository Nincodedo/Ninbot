package dev.nincodedo.ninbot.common.logging;

import dev.nincodedo.ninbot.common.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.logging.LogLevel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
@Accessors(chain = true)
public class ServerLogLevel extends BaseEntity {
    @Column(nullable = false)
    private String serverId;
    @Column(nullable = false)
    private LogLevel logLevel;
}
