package hr.kosani.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.Test;
import org.slf4j.Logger;

public class NamingConventionsTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("hr.kosani.archunit");

    @Test
    public void readMethodsInRepositoriesShouldStartWithFind() {
        methods().that().areDeclaredInClassesThat().areAnnotatedWith(Repository.class).and().arePublic()
                .should().haveNameMatching("find.*")
                .orShould().haveNameMatching("save.*")
                .orShould().haveNameMatching("delete.*")
                .check(classes);
    }

    @Test
    public void repositoriesHaveRepositorySuffix() {
        classes().that().areAnnotatedWith(Repository.class)
                .should().haveSimpleNameEndingWith("Repository")
                .check(classes);
    }

    @Test
    public void servicesHaveServiceSuffix() {
        classes().that().areAnnotatedWith(Service.class)
                .should().haveSimpleNameEndingWith("Service")
                .check(classes);
    }

    @Test
    public void controllersHaveControllerSuffix() {
        classes().that().areAnnotatedWith(Controller.class)
                .should().haveSimpleNameEndingWith("Controller")
                .check(classes);
    }

    @Test
    public void loggersShouldBeConstants() {
        fields().that().haveRawType(Logger.class)
                .should().bePrivate()
                .andShould().beStatic()
                .andShould().beFinal()
                .andShould().haveName("LOG")
                .because("this is a convention")
                .check(classes);
    }

    @Test
    public void interfacesShouldNotHaveCSharpPrefixI() {
        classes().that().areInterfaces().should(notBePrefixedWithI()).check(classes);
    }

    private ArchCondition<? super JavaClass> notBePrefixedWithI() {
        return new ArchCondition<JavaClass>("not have prefix I") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                DescribedPredicate<JavaClass> predicate = new DescribedPredicate<JavaClass>("has name starting with I") {
                    @Override
                    public boolean apply(JavaClass input) {
                        String className = input.getSimpleName();
                        return className.length() > 2 && className.charAt(0) == 'I'
                                && Character.isUpperCase(className.charAt(1));
                    }
                };
                boolean satisfied = predicate.apply(item);
                String message = String.format("Interface %s %s prefixed with 'I'",
                        item.getName(),
                        satisfied ? "is" : "is not");
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }
}
