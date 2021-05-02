package io.github.eylexlive.leaderboards.database;

import io.github.eylexlive.leaderboards.util.config.ConfigUtil;

import java.sql.*;

public abstract class Database {

    private Connection connection;

    public abstract Database init();

    public abstract void close();

    public boolean update(String sql) {
        try {
            connection.prepareStatement(sql).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean update(String sql, String str) {
        if (str != null)
            sql = sql.replaceFirst(str, ConfigUtil.getString("mysql.table-prefix") + str);

        try {
            connection.prepareStatement(sql).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ResultSet query(String sql, String str) {
        if (str != null)
            sql = sql.replaceFirst(str, ConfigUtil.getString("mysql.table-prefix") + str);

        try {
            final PreparedStatement statement = connection.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet query(String sql) {
        try {
            final PreparedStatement statement = connection.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validateData(String sql, String str) {
        try (ResultSet rs = query(sql, str)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
