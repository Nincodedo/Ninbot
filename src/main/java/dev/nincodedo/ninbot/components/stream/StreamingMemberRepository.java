package dev.nincodedo.ninbot.components.stream;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StreamingMemberRepository extends CrudRepository<StreamingMember, Long> {
    Optional<StreamingMember> findByUserIdAndGuildId(String userId, String guildId);
}
