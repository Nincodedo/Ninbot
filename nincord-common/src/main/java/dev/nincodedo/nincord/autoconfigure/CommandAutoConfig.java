package dev.nincodedo.nincord.autoconfigure;

import dev.nincodedo.nincord.command.AbstractCommandParser;
import dev.nincodedo.nincord.command.Command;
import dev.nincodedo.nincord.command.CommandListener;
import dev.nincodedo.nincord.command.CommandRegistration;
import dev.nincodedo.nincord.config.db.ConfigRepository;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.stats.StatManager;
import dev.nincodedo.nincord.stats.StatRepository;
import dev.nincodedo.nincord.util.ClassScanner;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
@ComponentScan("dev.nincodedo")
public class CommandAutoConfig {

    @Bean
    public ExecutorService commandParserThreadPool() {
        return Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
    }

    @Bean
    public CommandListener commandListener(List<Command> commands) {
        if (commands.isEmpty()) {
            log.trace("No commands found, not registering a Command Listener");
            return null;
        }
        var parserClassList = ClassScanner.findClassesThatExtend(AbstractCommandParser.class);
        log.trace("Found {} parsers, finding required ones", parserClassList.size());
        var requiredCommandParsers = parserClassList.stream()
                .map(aClass -> {
                    try {
                        return aClass.getDeclaredConstructor(ExecutorService.class);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(constructor -> {
                    try {
                        return constructor.newInstance(commandParserThreadPool());
                    } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(abstractCommandParser -> commands.stream()
                        .anyMatch(abstractCommandParser::isCommandMatchParser))
                .toList();
        log.trace("Ending with {} parsers", requiredCommandParsers.size());
        return new CommandListener(requiredCommandParsers, commands);
    }

    @Bean
    public CommandRegistration commandRegistration(List<Command> commands) {
        return new CommandRegistration(commands);
    }

    @Bean
    public StatManager statManager(StatRepository statRepository) {
        return new StatManager(statRepository);
    }

    @Bean
    public ConfigService configService(ConfigRepository configRepository) {
        return new ConfigService(configRepository);
    }
}
