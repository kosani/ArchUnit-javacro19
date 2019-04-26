package hr.kosani.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchitectureTest {

    private final JavaClasses projectClasses = new ClassFileImporter().importPackages("hr.kosani.archunit");

    @Test
    public void onlyDomainLayerDependsOnPersistenceLayer() {
        classes().that().resideInAPackage("..persistence..")
                .should().onlyHaveDependentClassesThat().resideInAnyPackage("..domain..", "..persistence..")
                .check(projectClasses);
    }

    @Test
    public void onlyPresentationLayerCanDependOnDomainLayer() {
        classes().that().resideInAPackage("..domain..")
                .should().onlyHaveDependentClassesThat().resideInAnyPackage("..domain..", "..presentation..")
                .check(projectClasses);
    }

    @Test
    public void noLayerCanDependOnPresentationLayer() {
        noClasses().that().resideInAnyPackage("..domain..", "..persistence..")
                .should().dependOnClassesThat().resideInAPackage("..presentation..")
                .check(projectClasses);
    }

    @Test
    public void layersOnlyCommunicateThroughInterfaces() {
        noClasses().that().resideInAPackage("..domain..")
                .should().accessClassesThat(areImplementationsOfLayer("persistence"))
                .check(projectClasses);

        noClasses().that().resideInAPackage("..presentation..")
                .should().accessClassesThat(areImplementationsOfLayer("domain"))
                .check(projectClasses);

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
    public void layerDependencies() {
        layeredArchitecture()
                .layer("Presentation").definedBy("hr.kosani.archunit..presentation..")
                .layer("Domain").definedBy("hr.kosani.archunit..domain..")
                .layer("Persistence").definedBy("hr.kosani.archunit..persistence..")

                .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Presentation", "Domain")
                .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Domain", "Persistence")
                .check(projectClasses);
    }

    @Test
    public void onlyPersistenceLayerCanAccessJavaSql() {
        JavaClasses projectClassesWithSql = new ClassFileImporter().importPackages("hr.kosani.archunit", "java.sql");
        noClasses()
                .that().resideInAPackage("hr.kosani.archunit..")
                .and().resideOutsideOfPackage("hr.kosani.archunit..persistence..")
                .should().accessClassesThat().resideInAnyPackage("java.sql..")
                .check(projectClassesWithSql);
    }
}
