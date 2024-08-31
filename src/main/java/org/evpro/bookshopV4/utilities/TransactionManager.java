package org.evpro.bookshopV4.utilities;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    public static void executeInTransaction(Connection connection, SqlFunction function) throws SQLException {
        try {
            connection.setAutoCommit(false);
            function.execute();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @FunctionalInterface
    public interface SqlFunction {
        void execute() throws SQLException;
    }
}
