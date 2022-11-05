package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface StreamingMemberRepository extends BaseRepository<StreamingMember> {
    @NotNull
    @Override
    default List<StreamingMember> findAll() {
        return findAllByDeleted(false);
    }

    @NotNull
    List<StreamingMember> findAllByDeleted(Boolean isDeleted);

    @NotNull
    default List<StreamingMember> findAllByTwitchUsernameIsNotNull() {
        return findAllByTwitchUsernameIsNotNullAndDeleted(false);
    }

    @NotNull
    List<StreamingMember> findAllByTwitchUsernameIsNotNullAndDeleted(Boolean isDeleted);

    @NotNull
    default List<StreamingMember> findAllByTwitchUsername(String twitchUsername) {
        return findAllByTwitchUsernameAndDeleted(twitchUsername, false);
    }

    @NotNull
    List<StreamingMember> findAllByTwitchUsernameAndDeleted(String twitchUsername, Boolean isDeleted);

    default Optional<StreamingMember> findByUserIdAndGuildId(String userId, String guildId) {
        return findByUserIdAndGuildIdAndDeleted(userId, guildId, false);
    }

    Optional<StreamingMember> findByUserIdAndGuildIdAndDeleted(String userId, String guildId, Boolean isDeleted);
}
