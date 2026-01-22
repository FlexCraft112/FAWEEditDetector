package me.flexcraft.faweeditdetector;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;

public class SQLiteManager {

    private final JavaPlugin plugin;
    private Connection connection;

    public SQLiteManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "fawe_logs.db");
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + dbFile.getAbsolutePath()
            );

            createTable();

        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка SQLite!");
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS fawe_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player TEXT,
                    group_name TEXT,
                    world TEXT,
                    command TEXT,
                    time INTEGER
                );
                """;

        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        }
    }

    public void insertLog(String player, String group, String world, String command) {
        String sql = """
                INSERT INTO fawe_logs (player, group_name, world, command, time)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player);
            ps.setString(2, group);
            ps.setString(3, world);
            ps.setString(4, command);
            ps.setLong(5, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException ignored) {}
    }
}
