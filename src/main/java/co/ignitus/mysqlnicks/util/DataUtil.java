package co.ignitus.mysqlnicks.util;

import co.ignitus.mysqlnicks.MySQLNicks;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataUtil {

    private static final MySQLNicks mySQLNicks = MySQLNicks.getInstance();

    @Getter
    private static final HikariDataSource dataSource;

    static {
        dataSource = setupDatabase();
    }

    //Stores the database connection information
    private static HikariDataSource setupDatabase() {
        final FileConfiguration config = mySQLNicks.getConfig();
        final HikariDataSource dataSource = new HikariDataSource();
        final String host = config.getString("mysql.host", "");
        final int port = config.getInt("mysql.port", 3306);
        final String database = config.getString("mysql.database", "");
        final String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(config.getString("mysql.username", ""));
        dataSource.setPassword(config.getString("mysql.password", ""));
        dataSource.addDataSourceProperty("autoReconnect", "true");
        dataSource.addDataSourceProperty("autoReconnectForPools", "true");
        dataSource.addDataSourceProperty("interactiveClient", "true");
        dataSource.addDataSourceProperty("characterEncoding", "UTF-8");
        if (!config.getBoolean("mysql.ssl", true))
            dataSource.addDataSourceProperty("useSSL", "false");
        return dataSource;
    }

    //Attempts to create the necessary databases for the plugin
    public static boolean createDatabases() {
        try (Connection connection = getDataSource().getConnection()) {
            connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS mysqlnicks(" +
                            "`uuid` VARCHAR(255) UNIQUE NOT NULL," +
                            "`nickname` VARCHAR(255) DEFAULT NULL," +
                            "PRIMARY KEY (`uuid`))"
            ).execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a player has a saved nickname in the database
     *
     * @param uuid The uuid of the player
     * @return true if the player has a nickname. False if they don't
     */
    public static boolean hasNickname(UUID uuid) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `mysqlnicks` WHERE `uuid` = ?");
            statement.setString(1, uuid.toString());
            return statement.executeQuery().next();
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Get's the saved nickname of a specified player
     *
     * @param uuid The player's uuid
     * @return The nickname of a player, or null if they don't have a nickname
     */
    public static String getNickname(UUID uuid) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `mysqlnicks` WHERE `uuid` = ?");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (result.next())
                return result.getString("nickname");
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Attempts to set a player's nickname
     *
     * @param uuid     The player's uuid
     * @param nickname The desired nickname
     * @return true if the nickname was changed, or false if an error occurred
     */
    public static boolean setNickname(UUID uuid, String nickname) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `mysqlnicks`(`uuid`, `nickname`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `nickname` = ?"
            );
            statement.setString(1, uuid.toString());
            statement.setString(2, nickname);
            statement.setString(3, nickname);
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Attempts to simultaneously update multiple nicknames
     *
     * @param nicknames A map of nicknames to be updated.
     * @return true if the nicknames were updated, or false if the action failed
     */
    public static boolean updateMultipleNicknames(HashMap<UUID, String> nicknames) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `mysqlnicks`(`uuid`, `nickname`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `nickname` = ?"
            );
            for (Map.Entry<UUID, String> data : nicknames.entrySet()) {
                statement.setString(1, data.getKey().toString());
                statement.setString(2, data.getValue());
                statement.setString(3, data.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * @return A map of nicknames stored in the database.
     */
    public static HashMap<UUID, String> getSavedNicknames() {
        HashMap<UUID, String> nicknames = new HashMap<>();
        try (Connection connection = getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM `mysqlnicks`");
            while (result.next())
                nicknames.put(UUID.fromString(result.getString("uuid")), result.getString("nickname"));
        } catch (SQLException ignored) {
        }
        return nicknames;
    }
}
