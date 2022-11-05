package dev.nincodedo.ninbot.components.stream;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StreamingMemberRepository extends CrudRepository<StreamingMember, Long> {
    Optional<StreamingMember> findByUserIdAndGuildId(String userId, String guildId);

    @NotNull
    @Override
    List<StreamingMember> findAll();

    @NotNull
    List<StreamingMember> findAllByTwitchUsernameIsNotNull();

    @NotNull
    List<StreamingMember> findAllByTwitchUsername(String twitchUsername);
}
