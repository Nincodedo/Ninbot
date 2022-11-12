package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<NinbotUser> {

    Optional<NinbotUser> getFirstByUserId(String userId);
}
