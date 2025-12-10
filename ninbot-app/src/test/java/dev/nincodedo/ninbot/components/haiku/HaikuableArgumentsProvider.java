package dev.nincodedo.ninbot.components.haiku;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class HaikuableArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of("the the the the the the the the the the the the the the the the the", "the the the the the "
                        + "the the the the the the the the the the the 9",
                "Because Amazon continues to be a curse on my whole work life.", "Thank you Nintendo for Nintendo"
                        + " 64 it so a haiku").map(Arguments::of);
    }
}
