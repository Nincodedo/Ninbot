package dev.nincodedo.ninbot;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction;
import com.tngtech.archunit.library.GeneralCodingRules;
import dev.nincodedo.ninbot.common.BaseListenerAdapter;
import dev.nincodedo.ninbot.common.command.CommandNameEnum;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.persistence.Entity;
import java.util.List;


class VerifyArchitectureTest {

    JavaClasses ninbotClasses = new ClassFileImporter().importPackages("dev.nincodedo.ninbot");

    static List<ArchRule> generalRules() {
        return List.of(GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.because("All logging should go "
                        + "through Slf4j"),
                GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.because("That's illegal"),
                GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING.because("That's also illegal"));
    }

    static List<ArchRule> slashCommandRules() {
        final GivenClassesConjunction slashCommandClasses = ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Command")
                .and()
                .areNotInterfaces();
        return List.of(slashCommandClasses.should()
                        .implement(SlashCommand.class)
                        .because("Slash commands need the SlashCommand implementation to be auto registered"),
                slashCommandClasses.should()
                        .dependOnClassesThat(JavaClass.Predicates.simpleNameEndingWith("CommandName"))
                        .because("Slash commands should use CommandName enums"));
    }

    @Test
    void testCommandNameEnums() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("CommandName")
                .and()
                .areNotInterfaces()
                .should()
                .implement(CommandNameEnum.class)
                .andShould()
                .bePackagePrivate()
                .check(ninbotClasses);
    }

    @Test
    void testListenerClasses() {
        ArchRuleDefinition.classes()
                .that()
                .areNotInterfaces()
                .and()
                .haveSimpleNameEndingWith("Listener")
                .and()
                .haveSimpleNameNotStartingWith("Twitch")
                .should()
                .beAssignableTo(BaseListenerAdapter.class)
                .check(ninbotClasses);
    }

    @Test
    void testEntityClasses() {
        ArchRuleDefinition.classes()
                .that()
                .areAnnotatedWith(Entity.class)
                .should()
                .beAssignableTo(BaseEntity.class)
                .check(ninbotClasses);
    }

    @ParameterizedTest
    @MethodSource("slashCommandRules")
    void testSlashCommandClasses(ArchRule rule) {
        rule.check(ninbotClasses);
    }

    @ParameterizedTest
    @MethodSource("generalRules")
    void testGeneralCodingRules(ArchRule rule) {
        rule.check(ninbotClasses);
    }
}
