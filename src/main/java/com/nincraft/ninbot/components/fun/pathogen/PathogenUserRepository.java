package com.nincraft.ninbot.components.fun.pathogen;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PathogenUserRepository extends CrudRepository<PathogenUser, Long> {
    Optional<PathogenUser> getByUserIdAndServerId(String userId, String serverId);
    List<PathogenUser> getAllByUserIdIsIn(List<String> userIds);
}
