package hr.kosani.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureRules {

    private JavaClasses classes = new ClassFileImporter().importPackages("hr.kosani.archunit");

    @Test
    public void onlyDomainLayerCanDependOnPersistenceLayer() {
        classes()
                .that().resideInAPackage("..persistence..")
                .should().onlyHaveDependentClassesThat().resideInAnyPackage("..domain..", "..persistence..")
                .check(classes);
    }

    @Test
    public void onlyPresentationLayerCanDependOnDomainLayer() {
        classes()
                .that().resideInAPackage("..presentation..")
                .should().onlyHaveDependentClassesThat().resideInAnyPackage("..domain..", "..presentation..")
                .check(classes);
    }

    @Test
    public void noLayerShouldDependOnThePresentationLayer() {
        noClasses()
                .that().resideOutsideOfPackage("..presentation..")
                .should().dependOnClassesThat().resideInAPackage("..presentation..")
                .check(classes);
    }

    @Test
    public void onlyPersistenceLayerCanAccessJavaSql() {
        JavaClasses classesWithSql = new ClassFileImporter().importPackages("hr.kosani.archunit", "java.sql");
        noClasses()
                .that().resideInAPackage("hr.kosani.archunit..")
                .and().resideOutsideOfPackage("hr.kosani.archunit..persistence..")
                .should().accessClassesThat().resideInAnyPackage("java.sql..")
                .check(classesWithSql);
    }
}