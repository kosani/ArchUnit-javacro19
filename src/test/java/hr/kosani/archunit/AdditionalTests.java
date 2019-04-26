package hr.kosani.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import org.junit.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

public class AdditionalTests {

    private final JavaClasses projectClasses = new ClassFileImporter().importPackages("hr.kosani.archunit");


    @Test
    public void preferJavaTimeToJavaDate() {
        ArchCondition<? super JavaClass> useJavaDate =
                dependOnClassesThat(assignableTo(java.util.Date.class)).as("use java.util.Date");
        noClasses().should(useJavaDate).because("modern Java projects use the [java.time] API instead").check(projectClasses);
    }

    @Test
    public void classesShouldOnlyUseSlf4jForLogging() {
        noClasses().should().dependOnClassesThat().resideInAPackage("org.apache.logging..").check(projectClasses);
    }

    // Some generic rules

    @Test
    public void classesDoNotThrowGenericException() {
        NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(projectClasses);
    }

    @Test
    public void noClassesShouldAccessStandardStream() {
        NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(projectClasses);
    }

}