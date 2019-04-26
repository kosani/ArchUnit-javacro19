package hr.kosani.archunit.persistence;

public class DataAccessException extends RuntimeException {

    DataAccessException(Throwable cause) {
        super(cause);
    }
}
