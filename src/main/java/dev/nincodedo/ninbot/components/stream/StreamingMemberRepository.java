package dev.nincodedo.ninbot.components.stream;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface StreamingMemberRepository extends BaseRepository<StreamingMember> {
    Optional<StreamingMember> findByUserIdAndGuildId(String userId, String guildId);
    //TODO add deleted method
    @NotNull
    @Override
    List<StreamingMember> findAll();
    //TODO add deleted method
    @NotNull
    List<StreamingMember> findAllByTwitchUsernameIsNotNull();
    //TODO add deleted method
    @NotNull
    List<StreamingMember> findAllByTwitchUsername(String twitchUsername);

    default Optional<StreamingMember> findByUserIdAndGuildId(String userId, String guildId) {
        return findByUserIdAndGuildIdAndDeleted(userId, guildId, false);
    }

    Optional<StreamingMember> findByUserIdAndGuildIdAndDeleted(String userId, String guildId, Boolean isDeleted);
}
