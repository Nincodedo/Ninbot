package dev.nincodedo.nincord;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Disabled("Until ArchUnit updates to support Java 20+")
class VerifyArchitectureTest {

    JavaClasses nincordClasses = new ClassFileImporter().importPackages("dev.nincodedo.nincord");

    static List<ArchRule> generalRules() {
        return List.of(GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.because("All logging should go "
                        + "through Slf4j"),
                GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.because("That's illegal"),
                GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING.because("That's also illegal"));
    }

    @Test
    void testSpringComponents() {
        ArchRuleDefinition.classes()
                .should().notBeAnnotatedWith(Component.class).check(nincordClasses);
    }

    @ParameterizedTest
    @MethodSource("generalRules")
    void testGeneralCodingRules(ArchRule rule) {
        rule.check(nincordClasses);
    }
}
