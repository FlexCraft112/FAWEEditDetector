package me.flexcraft.faweeditdetector;

import java.io.File;
import java.sql.*;

public class SQLiteManager {

    private final FAWEditDetector plugin;
    private Connection connection;

    public SQLiteManager(FAWEditDetector plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "database.db");
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            plugin.getLogger().info("SQLite подключена");
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка подключения SQLite");
            e.printStackTrace();
        }
    }

    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS fawe_logs (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              player TEXT,
              uuid TEXT,
              group_name TEXT,
              world TEXT,
              blocks INTEGER,
              x INTEGER,
              y INTEGER,
              z INTEGER,
              time INTEGER
            );
        """;

        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertLog(String player, String uuid, String group,
                          String world, int blocks, int x, int y, int z) {

        String sql = """
            INSERT INTO fawe_logs
            (player, uuid, group_name, world, blocks, x, y, z, time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player);
            ps.setString(2, uuid);
            ps.setString(3, group);
            ps.setString(4, world);
            ps.setInt(5, blocks);
            ps.setInt(6, x);
            ps.setInt(7, y);
            ps.setInt(8, z);
            ps.setLong(9, System.currentTimeMillis());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cleanupOldLogs(int days) {
        long limit = System.currentTimeMillis() - (days * 86400000L);

        try (PreparedStatement ps =
                     connection.prepareStatement("DELETE FROM fawe_logs WHERE time < ?")) {
            ps.setLong(1, limit);
            int removed = ps.executeUpdate();
            plugin.getLogger().info("SQLite очистка: удалено " + removed + " старых логов");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
