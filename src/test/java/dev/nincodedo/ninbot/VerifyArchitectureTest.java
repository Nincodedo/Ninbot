package dev.nincodedo.ninbot;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import dev.nincodedo.ninbot.components.command.SlashCommand;
import org.junit.jupiter.api.Test;


class VerifyArchitectureTest {
    @Test
    void testCommandClasses() {
        JavaClasses ninbotClasses = new ClassFileImporter().importPackages("dev.nincodedo.ninbot");
        ArchRule rule = ArchRuleDefinition.classes().that().haveNameMatching(".*Command")
                .and().haveNameNotMatching("Abstract.*")
                .and().areNotInterfaces()
                .should().beAssignableTo(SlashCommand.class);
        rule.check(ninbotClasses);
    }
}
