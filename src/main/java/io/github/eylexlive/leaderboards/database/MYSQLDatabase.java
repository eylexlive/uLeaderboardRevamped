package io.github.eylexlive.leaderboards.database;


import com.zaxxer.hikari.HikariDataSource;
import io.github.eylexlive.leaderboards.util.config.ConfigUtil;

import java.sql.SQLException;

public class MYSQLDatabase extends Database {

    final HikariDataSource dataSource = new HikariDataSource();

    @Override
    public Database init() {
        dataSource.setPoolName("uLeaderboardsDBPool");
        dataSource.setJdbcUrl("jdbc:mysql://" +
                        ConfigUtil.getString("mysql.host") +
                        ":" +
                        ConfigUtil.getInt("mysql.port") +
                        "/" +
                        ConfigUtil.getString("mysql.database")
        );

        dataSource.setUsername(ConfigUtil.getString("mysql.username"));
        dataSource.setPassword(ConfigUtil.getString("mysql.password"));

        dataSource.addDataSourceProperty("autoReconnect", "true");
        dataSource.addDataSourceProperty("autoReconnectForPools", "true");

        dataSource.addDataSourceProperty("characterEncoding", "UTF-8");
        dataSource.addDataSourceProperty("useSSL", String.valueOf(ConfigUtil.getBoolean("mysql.use-ssl")));

        try {
            setConnection(dataSource.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void close() {
        if (!dataSource.isClosed())
            dataSource.close();
    }
}
