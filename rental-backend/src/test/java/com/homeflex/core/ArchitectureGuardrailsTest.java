package com.homeflex.core;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureGuardrailsTest {

    @Test
    void controllersShouldNotAccessRepositoriesDirectly() {
        var classes = new ClassFileImporter().importPackages("com.homeflex");
        ArchRule rule = noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAPackage("..repository..");
        rule.check(classes);
    }
}
