package com.nincraft.ninbot.components.fun.pathogen;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PathogenRepository extends CrudRepository<PathogenUser, Long> {
    Optional<PathogenUser> getByUserId(String userId);
}
