package hr.kosani.archunit;

import javax.inject.Singleton;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Singleton
@Retention(RUNTIME)
public @interface Controller {
}
