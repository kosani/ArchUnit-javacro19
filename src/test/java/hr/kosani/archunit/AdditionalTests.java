package hr.kosani.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.ImportOptions;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.lang.ArchCondition;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class AdditionalTests {

    private final JavaClasses projectClasses = new ClassFileImporter().importPackages("hr.kosani.archunit");


    private static final ArchCondition<? super JavaClass> USE_JAVA_DATE = dependOnClassesThat(assignableTo(java.util.Date.class))
            .as("use java.uti.Date");

    @Test
    public void preferJavaTimeToJavaDate() {
        noClasses().should(USE_JAVA_DATE).because("modern Java projects use the [java.time] API instead").check(projectClasses);
    }

    @Test
    public void classesShouldOnlyUseSlf4jForLogging() {
        noClasses().should().dependOnClassesThat().resideInAPackage("org.apache.logging..").check(projectClasses);
    }

}
