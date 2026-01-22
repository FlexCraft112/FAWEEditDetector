package me.flexcraft.faweeditdetector;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class MySQLManager {

    private final FAWEditDetector plugin;
    private Connection connection;

    public MySQLManager(FAWEditDetector plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            FileConfiguration c = plugin.getConfig();

            String url = "jdbc:mysql://" +
                    c.getString("mysql.host") + ":" +
                    c.getInt("mysql.port") + "/" +
                    c.getString("mysql.database") +
                    "?useSSL=" + c.getBoolean("mysql.useSSL") +
                    "&characterEncoding=utf8";

            connection = DriverManager.getConnection(
                    url,
                    c.getString("mysql.user"),
                    c.getString("mysql.password")
            );

            plugin.getLogger().info("MySQL подключён.");

        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка MySQL!");
            e.printStackTrace();
        }
    }

    public void createTable() {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS fawe_logs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player VARCHAR(32),
                    `group` VARCHAR(32),
                    command TEXT,
                    world VARCHAR(32),
                    x INT,
                    y INT,
                    z INT,
                    time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertLog(String player, String group, String command,
                          String world, int x, int y, int z) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO fawe_logs (player, `group`, command, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?)"
        )) {
            ps.setString(1, player);
            ps.setString(2, group);
            ps.setString(3, command);
            ps.setString(4, world);
            ps.setInt(5, x);
            ps.setInt(6, y);
            ps.setInt(7, z);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (Exception ignored) {}
    }
}
