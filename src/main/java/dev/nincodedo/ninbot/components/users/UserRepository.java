package dev.nincodedo.ninbot.components.users;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<NinbotUser, Long> {
    @NotNull List<NinbotUser> findAll();

    Optional<NinbotUser> getFirstByUserId(String userId);
}
