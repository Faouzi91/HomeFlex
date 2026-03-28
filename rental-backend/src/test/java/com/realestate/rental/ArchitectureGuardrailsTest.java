package com.realestate.rental;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureGuardrailsTest {

    @Test
    void controllersShouldNotAccessRepositoriesDirectly() {
        var classes = new ClassFileImporter().importPackages("com.realestate.rental");
        ArchRule rule = noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAPackage("..repository..");
        rule.check(classes);
    }
}
