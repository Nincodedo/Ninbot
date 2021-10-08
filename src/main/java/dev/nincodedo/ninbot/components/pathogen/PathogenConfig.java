package dev.nincodedo.ninbot.components.pathogen;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathogenConfig {
    @Getter
    private static final String INFECTED_ROLE_NAME = "infected";
    @Getter
    private static final String VACCINATED_ROLE_NAME = "vaccinated";
}
