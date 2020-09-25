package dev.nincodedo.ninbot.components.event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface EventRepository extends CrudRepository<Event, Long> {
}
