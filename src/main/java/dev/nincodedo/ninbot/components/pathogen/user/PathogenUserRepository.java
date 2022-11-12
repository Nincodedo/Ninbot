package dev.nincodedo.ninbot.components.pathogen.user;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.List;

public interface PathogenUserRepository extends BaseRepository<PathogenUser> {
    PathogenUser getByUserIdAndServerId(String userId, String serverId);

    List<PathogenUser> getAllByUserIdIsIn(List<String> userIds);
}
