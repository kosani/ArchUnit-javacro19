package hr.kosani.archunit.persistence;

import javax.inject.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

@Singleton
class DatabaseConnection {
    private static final String JDBC_RESOURCE_NAME = "jdbc/myJdbc";
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private Supplier<Connection> connectionSupplier;

    private DatabaseConnection() {
        try {
            Context initContext = new InitialContext();
            Context webContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) webContext.lookup(JDBC_RESOURCE_NAME);
            connectionSupplier = () -> {
                try {
                    return dataSource.getConnection();
                } catch (SQLException e) {
                    throw new DataAccessException(e);
                }
            };
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }

    static DatabaseConnection getInstance() {
        return DatabaseConnection.INSTANCE;
    }

    Connection getConnection() {
        return connectionSupplier.get();
    }

}
