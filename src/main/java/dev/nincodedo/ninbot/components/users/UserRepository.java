package dev.nincodedo.ninbot.components.users;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<NinbotUser, Long> {
    Optional<NinbotUser> getFirstByUserId(String userId);
}
