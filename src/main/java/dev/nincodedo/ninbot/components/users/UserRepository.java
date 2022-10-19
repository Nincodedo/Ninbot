package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<NinbotUser> {

    default Optional<NinbotUser> getFirstByUserId(String userId) {
        return getFirstByUserIdAndDeleted(userId, false);
    }

    Optional<NinbotUser> getFirstByUserIdAndDeleted(String userId, Boolean isDeleted);
}
