package com.thstudio.project.dao;

import java.sql.*;

/**
 * Database class that manages a database connection.
 * Implements the Singleton pattern in order to have a single instance of the Database.
 */
public class Database {
    private static String dbName;
    private static String dbTestName;
    private static String dbUser;
    private static String dbPassword;
    private static String dbHost;
    private static String dbPort;
    private static String dbUrl;
    /**
     *Private constructor to avoid instantiation, since this class is used only to connect to the database
     * We follow the Singleton pattern
     */
    private Database() {
        dbUrl = "";
    }

    /**
     * Set the default database name
     *
     * @param dbName Name of the database file
     */
    public static void setDbName(String dbName) {
        Database.dbName = dbName;
    }

    /**
     * Set the test database  name
     *
     * @param dbTestName Name of the test database file
     */

    public static void setDbTestName(String dbTestName) {
        Database.dbTestName = dbTestName;
    }

    /**
     * Set the database user
     *
     * @param dbUser Name of the database user
     */
    public static void setDbUser(String dbUser) {
        Database.dbUser = dbUser;
    }

    /**
     * Set the database password
     *
     * @param dbPassword Name of the database password
     */
    public static void setDbPassword(String dbPassword) {
        Database.dbPassword = dbPassword;
    }

    /**
     * Set the database host
     *
     * @param dbHost Name of the database host
     */
    public static void setDbHost(String dbHost) {
        Database.dbHost = dbHost;
    }

    /**
     * Set the database port
     *
     * @param dbPort Name of the database port
     */
    public static void setDbPort(String dbPort) {
        Database.dbPort = dbPort;
    }

    /**
     * Set the database url to deault
     *
     */
    public static void useDefaultDb() {
        dbUrl = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword;
    }

    /**
     * Set the database url to test
     *
     */
    public static void useTestDb() {
        dbUrl = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/" + dbTestName + "?user=" + dbUser + "&password=" + dbPassword;
    }

    /**
     * Get the default connection instance
     *
     * @return Connection to the mariaDb database
     */

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * This method closes the given connection
     *
     * @param connection is the connection to close
     */
    public static void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    public static boolean testConnection(boolean useTestDb, boolean debug) {
        if (useTestDb) {
            useTestDb();
        } else {
            useDefaultDb();
        }
        boolean success = false;
        if (debug) {
            System.out.println("DB URL: " + dbUrl);
        }
        try {
            Connection connection = getConnection();
            System.out.println("Connection to the database has been established.");
            closeConnection(connection);
            success = true;
        } catch (SQLException e) {
            System.out.println("Connection to the database has failed.");
            e.printStackTrace();
        }
        return success;
    }
}
