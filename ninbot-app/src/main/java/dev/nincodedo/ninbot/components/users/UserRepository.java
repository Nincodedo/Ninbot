package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<NinbotUser> {

    Optional<NinbotUser> getFirstByUserId(String userId);
}
