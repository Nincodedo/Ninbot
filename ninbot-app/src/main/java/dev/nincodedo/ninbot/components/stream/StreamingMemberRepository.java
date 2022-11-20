package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.nincord.persistence.BaseRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface StreamingMemberRepository extends BaseRepository<StreamingMember> {
    Optional<StreamingMember> findByUserIdAndGuildId(String userId, String guildId);

    @NotNull
    List<StreamingMember> findAllByTwitchUsernameIsNotNull();

    @NotNull
    List<StreamingMember> findAllByTwitchUsername(String twitchUsername);
}
