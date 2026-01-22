package me.flexcraft.faweeditdetector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLManager {

    private Connection connection;
    private final FAWEditDetector plugin;

    public MySQLManager(FAWEditDetector plugin) {
        this.plugin = plugin;
    }

    public boolean connect() {
        if (!plugin.getConfig().getBoolean("mysql.enabled")) {
            plugin.getLogger().warning("MySQL отключён в config.yml");
            return false;
        }

        try {
            String host = plugin.getConfig().getString("mysql.host");
            int port = plugin.getConfig().getInt("mysql.port");
            String database = plugin.getConfig().getString("mysql.database");
            String user = plugin.getConfig().getString("mysql.user");
            String password = plugin.getConfig().getString("mysql.password");
            boolean ssl = plugin.getConfig().getBoolean("mysql.useSSL");

            String url =
                    "jdbc:mysql://" + host + ":" + port + "/" + database +
                    "?useSSL=" + ssl +
                    "&useUnicode=true" +
                    "&characterEncoding=utf8" +
                    "&autoReconnect=true" +
                    "&serverTimezone=UTC";

            connection = DriverManager.getConnection(url, user, password);

            if (connection == null) {
                plugin.getLogger().severe("MySQL connection == null");
                return false;
            }

            createTable();

            plugin.getLogger().info("MySQL подключена успешно");
            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка подключения к MySQL!");
            e.printStackTrace();
            connection = null;
            return false;
        }
    }

    private void createTable() {
        if (connection == null) {
            plugin.getLogger().severe("MySQL не подключена, таблица не создана");
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS fawe_edits (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "player VARCHAR(32) NOT NULL," +
                            "uuid VARCHAR(36) NOT NULL," +
                            "group_name VARCHAR(32) NOT NULL," +
                            "world VARCHAR(64) NOT NULL," +
                            "blocks BIGINT NOT NULL," +
                            "min_x INT NOT NULL," +
                            "min_y INT NOT NULL," +
                            "min_z INT NOT NULL," +
                            "max_x INT NOT NULL," +
                            "max_y INT NOT NULL," +
                            "max_z INT NOT NULL," +
                            "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка создания таблицы MySQL");
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
