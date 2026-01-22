package me.flexcraft.faweeditdetector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class MySQLManager {

    private Connection connection;
    private final FAWEditDetector plugin;

    public MySQLManager(FAWEditDetector plugin) {
        this.plugin = plugin;
    }

    public boolean connect() {
        try {
            String host = plugin.getConfig().getString("mysql.host");
            int port = plugin.getConfig().getInt("mysql.port");
            String database = plugin.getConfig().getString("mysql.database");
            String user = plugin.getConfig().getString("mysql.user");
            String password = plugin.getConfig().getString("mysql.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
                    "?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";

            connection = DriverManager.getConnection(url, user, password);

            createTable();
            plugin.getLogger().info("MySQL подключена успешно");
            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка MySQL");
            e.printStackTrace();
            return false;
        }
    }

    private void createTable() throws Exception {
        Statement st = connection.createStatement();
        st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS fawe_edits (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "player VARCHAR(32)," +
                        "group_name VARCHAR(32)," +
                        "world VARCHAR(64)," +
                        "blocks BIGINT," +
                        "min_x INT," +
                        "min_y INT," +
                        "min_z INT," +
                        "max_x INT," +
                        "max_y INT," +
                        "max_z INT," +
                        "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        );
        st.close();
    }

    public void insertLog(String player, String group, String world,
                          long blocks,
                          int minX, int minY, int minZ,
                          int maxX, int maxY, int maxZ) {

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO fawe_edits " +
                            "(player, group_name, world, blocks, min_x, min_y, min_z, max_x, max_y, max_z) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?)"
            );

            ps.setString(1, player);
            ps.setString(2, group);
            ps.setString(3, world);
            ps.setLong(4, blocks);
            ps.setInt(5, minX);
            ps.setInt(6, minY);
            ps.setInt(7, minZ);
            ps.setInt(8, maxX);
            ps.setInt(9, maxY);
            ps.setInt(10, maxZ);

            ps.executeUpdate();
            ps.close();

        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка записи FAWE лога");
            e.printStackTrace();
        }
    }
}
