* Remark 1: Direct access to repositories, services are skipped.
```java
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

```

..or better..

```java
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
```

* Remark 2: Persistence-specific exceptions shouldn't leak to presentation layer.

```java
@Test
public void onlyPersistenceLayerCanAccessJavaSql() {
    JavaClasses projectClassesWithSql = new ClassFileImporter().importPackages("hr.kosani.archunit", "java.sql");
    noClasses()
            .that().resideInAPackage("hr.kosani.archunit..")
            .and().resideOutsideOfPackage("hr.kosani.archunit..persistence..")
            .should().accessClassesThat().resideInAnyPackage("java.sql..")
            .check(projectClassesWithSql);
}
```

* Remark 3: Should depend on interface rather than implementation.

```java
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

```

* Remark 4: Read methods should be called by find.
```java
@Test
public void readMethodsInRepositoriesShouldStartWithFind() {
    methods().that().areDeclaredInClassesThat().areAnnotatedWith(Repository.class).and().arePublic()
            .should().haveNameMatching("find.*")
            .orShould().haveNameMatching("save.*")
            .orShould().haveNameMatching("delete.*")
            .as("repository methods names should only be start with find, save or delete*")
            .check(classes);
}

```

* Remark 5: Logger API should be preferred to logger implementation.

```java
@Test
public void classesShouldOnlyUseSlf4jForLogging() {
    noClasses().should().dependOnClassesThat().resideInAPackage("org.apache.logging..").check(projectClasses);
}
```

* Remark 6: Logger should be final, static and named with upper_case LOG.

```java
@Test
public void loggersShouldBeConstants() {
    fields().that().haveRawType(Logger.class)
            .should().bePrivate()
            .andShould().beStatic()
            .andShould().beFinal()
            .andShould().haveName("LOG")
            .because("we agreed on this convention")
            .check(classes);}
```

* Remark 7: App shouldn't write to standard streams.

```java
@Test
public void noClassesShouldAccessStandardStream() {
    NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(projectClasses);
}
```

* Remark 8: Generic exceptions shouldn't be thrown.

```java
@Test
public void classesDoNotThrowGenericException() {
    NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(projectClasses);
}
```

* Remark 8: Prefer java.time to Date.

```java
@Test
public void preferJavaTimeToJavaDate() {
    ArchCondition<? super JavaClass> useJavaDate =
            dependOnClassesThat(assignableTo(java.util.Date.class)).as("use java.util.Date");
    noClasses().should(useJavaDate).because("modern Java projects use the [java.time] API instead").check(projectClasses);
}
```

* Remark 9: Repositories should not have 'I' prefix.

```java

@Test
public void interfacesShouldNotHavePrefixI() {
    classes().that().areInterfaces().should(not(havePrefixI))
            .because("we have more sophisticated IDE-s that tell us when a class is an interface")
            .check(classes);
}


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
```