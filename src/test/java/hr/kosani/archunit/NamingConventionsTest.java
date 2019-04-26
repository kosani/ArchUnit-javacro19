package hr.kosani.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.Test;
import org.slf4j.Logger;

import static com.tngtech.archunit.lang.conditions.ArchConditions.not;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class NamingConventionsTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("hr.kosani.archunit");

    private ArchCondition<JavaClass> havePrefixI = new ArchCondition<JavaClass>("have prefix I") {
        @Override
        public void check(JavaClass item, ConditionEvents events) {
            String className = item.getSimpleName();
            boolean satisfied = className.length() > 2 && className.charAt(0) == 'I'
                    && Character.isUpperCase(className.charAt(1));
            String message = String.format("Interface %s %s prefixed with 'I'",
                    item.getName(),
                    satisfied ? "is" : "is not");
            events.add(new SimpleConditionEvent(item, satisfied, message));
        }
    };

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
                .because("we agreed on this convention")
                .check(classes);
    }

    @Test
    public void interfacesShouldNotHavePrefixI() {
        classes().that().areInterfaces().should(not(havePrefixI))
                .because("we have more sophisticated IDE-s that tell us when a class is an interface")
                .check(classes);
    }
}
