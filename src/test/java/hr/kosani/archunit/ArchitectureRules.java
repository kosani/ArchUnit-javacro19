package hr.kosani.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;
import org.slf4j.Logger;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

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

    @Test
    public void readMethodsInRepositoriesShouldStartWithFind() {
        methods().that().areDeclaredInClassesThat().areAnnotatedWith(Repository.class).and().arePublic()
                .should().haveNameMatching("find.*")
                .orShould().haveNameMatching("save.*")
                .orShould().haveNameMatching("delete.*")
                .as("repository methods names should only be start with find, save or delete*")
                .check(classes);
    }

    @Test
    public void layersOnlyCommunicateThroughInterfaces() {
        noClasses().that().resideInAPackage("..domain..")
                .should().accessClassesThat(areImplementationsOfLayer("persistence"))
                .check(classes);

        noClasses().that().resideInAPackage("..presentation..")
                .should().accessClassesThat(areImplementationsOfLayer("domain"))
                .check(classes);
    }

    private DescribedPredicate<JavaClass> areImplementationsOfLayer(String layer) {
        return resideInAPackage(String.format("..%s..", layer)).and(areNotInterfaces());
    }

    private DescribedPredicate<? super JavaClass> areNotInterfaces() {
        return new DescribedPredicate<JavaClass>("are not interfaces") {
            @Override
            public boolean apply(JavaClass input) {
                return !input.isInterface();
            }
        };
    }

    @Test
    public void classesShouldOnlyUseSlf4jForLogging() {
        noClasses().should().dependOnClassesThat().resideInAPackage("org.apache.logging..").check(classes);
    }

    @Test
    public void loggersShouldBeConstants() {
        fields().that().haveRawType(Logger.class)
                .should().bePrivate()
                .andShould().beStatic()
                .andShould().beFinal()
                .andShould().haveName("LOG")
                .because("we agreed on this convention")
                .check(classes);
    }
}