package dev.nincodedo.ninbot.components.haiku;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class NonhaikuableArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of("too short",
                        "Because Amazon continues to be a curse on my work life.",
                        "that is a diaminopropyltetramethylenediamine", "welcome welcome welcome place place place "
                                + "place "
                                + "place place place place place place place", "place place place place place welcome"
                                + " welcome"
                                + " welcome welcome place place place place", "place place place place place place "
                                + "place "
                                + "place place place place place welcome welcome", "", "wow numbers like this 51261")
                .map(Arguments::of);
    }
}
