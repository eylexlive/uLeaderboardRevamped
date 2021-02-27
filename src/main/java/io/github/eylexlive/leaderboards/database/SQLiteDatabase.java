package io.github.eylexlive.leaderboards.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase extends Database {

    @Override
    public Database init() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
           throw new IllegalStateException("SQLite JDBC Driver not found!");
        }

        try {
            setConnection(DriverManager.getConnection("jdbc:sqlite:plugins/uLeaderboards/database.db"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public void close() {
        if (getConnection() != null) {
            try {
                getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
