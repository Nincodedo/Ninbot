package com.nincraft.ninbot.components.countdown;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountdownRepository extends CrudRepository<Countdown, Long> {
    List<Countdown> findByServerId(String serverId);
}
