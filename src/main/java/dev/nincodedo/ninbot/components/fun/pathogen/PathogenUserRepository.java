package dev.nincodedo.ninbot.components.fun.pathogen;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PathogenUserRepository extends CrudRepository<PathogenUser, Long> {
    PathogenUser getByUserIdAndServerId(String userId, String serverId);

    List<PathogenUser> getAllByUserIdIsIn(List<String> userIds);
}
