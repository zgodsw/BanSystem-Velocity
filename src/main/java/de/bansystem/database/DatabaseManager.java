package de.bansystem.database;

import de.bansystem.BanSystem;

import java.sql.*;

public class DatabaseManager {

    private final BanSystem plugin;
    private Connection connection;

    public DatabaseManager(BanSystem plugin) {
        this.plugin = plugin;
    }

    public boolean connect() {
        try {
            String host = plugin.getConfigManager().getMySQLHost();
            int port = plugin.getConfigManager().getMySQLPort();
            String database = plugin.getConfigManager().getMySQLDatabase();
            String username = plugin.getConfigManager().getMySQLUsername();
            String password = plugin.getConfigManager().getMySQLPassword();

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database
                            + "?autoReconnect=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    username, password
            );

            plugin.getLogger().info("MySQL Verbindung erfolgreich hergestellt!");
            createTables();
            return true;

        } catch (ClassNotFoundException e) {
            plugin.getLogger().error("MySQL Driver nicht gefunden!", e);
            return false;
        } catch (SQLException e) {
            plugin.getLogger().error("MySQL Verbindung fehlgeschlagen!", e);
            return false;
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("MySQL Verbindung geschlossen.");
            }
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Schließen der Verbindung!", e);
        }
    }

    public boolean checkConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                plugin.getLogger().warn("Datenbankverbindung verloren - versuche Reconnect...");
                return connect();
            }
            Statement stmt = connection.createStatement();
            stmt.executeQuery("SELECT 1");
            stmt.close();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Prüfen der Datenbankverbindung!", e);
            return connect();
        }
    }

    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Bans Tabelle
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bans (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "banned_by VARCHAR(16) NOT NULL," +
                    "banned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "expires_at TIMESTAMP NULL," +
                    "permanent BOOLEAN DEFAULT FALSE," +
                    "active BOOLEAN DEFAULT TRUE," +
                    "unbanned_by VARCHAR(16) NULL," +
                    "unbanned_at TIMESTAMP NULL," +
                    "INDEX idx_uuid (uuid)," +
                    "INDEX idx_active (active)" +
                    ")");

            // Mutes Tabelle
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS mutes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "muted_by VARCHAR(16) NOT NULL," +
                    "muted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "expires_at TIMESTAMP NULL," +
                    "permanent BOOLEAN DEFAULT FALSE," +
                    "active BOOLEAN DEFAULT TRUE," +
                    "unmuted_by VARCHAR(16) NULL," +
                    "unmuted_at TIMESTAMP NULL," +
                    "INDEX idx_uuid (uuid)," +
                    "INDEX idx_active (active)" +
                    ")");

            // Kicks Tabelle
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS kicks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "kicked_by VARCHAR(16) NOT NULL," +
                    "kicked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "INDEX idx_uuid (uuid)" +
                    ")");

            // Reports Tabelle
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS reports (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "reported_uuid VARCHAR(36) NOT NULL," +
                    "reported_name VARCHAR(16) NOT NULL," +
                    "reporter_uuid VARCHAR(36) NOT NULL," +
                    "reporter_name VARCHAR(16) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "status VARCHAR(20) DEFAULT 'OPEN'," +
                    "handled_by VARCHAR(16) NULL," +
                    "handled_at TIMESTAMP NULL" +
                    ")");

            // Notes Tabelle
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS notes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "note TEXT NOT NULL," +
                    "created_by VARCHAR(16) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Auth Codes Tabelle
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS auth_codes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "auth_code VARCHAR(6) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "used BOOLEAN DEFAULT FALSE," +
                    "INDEX idx_code (auth_code)" +
                    ")");

            stmt.close();
            plugin.getLogger().info("Datenbank-Tabellen erfolgreich erstellt!");
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Erstellen der Datenbank-Tabellen!", e);
        }
    }

    public Connection getConnection() {
        checkConnection();
        return connection;
    }
}