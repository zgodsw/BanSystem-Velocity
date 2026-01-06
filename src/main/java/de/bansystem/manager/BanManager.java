package de.bansystem.manager;

import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import de.bansystem.utils.TimeUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.*;
import java.util.UUID;

public class BanManager {

    private final BanSystem plugin;

    public BanManager(BanSystem plugin) {
        this.plugin = plugin;
    }

    public void banPlayer(UUID uuid, String playerName, String reason, String bannedBy, long duration) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bans (uuid, player_name, reason, banned_by, expires_at, permanent) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, uuid.toString());
            ps.setString(2, playerName);
            ps.setString(3, reason);
            ps.setString(4, bannedBy);

            if (duration == -1) {
                ps.setNull(5, Types.TIMESTAMP);
                ps.setBoolean(6, true);
            } else {
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis() + duration));
                ps.setBoolean(6, false);
            }

            ps.executeUpdate();
            ps.close();

            // Spieler kicken falls online
            plugin.getServer().getPlayer(uuid).ifPresent(player -> {
                player.disconnect(Component.text("§cDu wurdest gebannt!\n§7Grund: §f" + reason));
            });

        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Bannen von " + playerName, e);
        }
    }

    public void unbanPlayer(UUID uuid, String unbannedBy) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bans SET active = FALSE, unbanned_by = ?, unbanned_at = NOW() WHERE uuid = ? AND active = TRUE"
            );
            ps.setString(1, unbannedBy);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Entbannen", e);
        }
    }

    public boolean isBanned(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM bans WHERE uuid = ? AND active = TRUE ORDER BY banned_at DESC LIMIT 1"
            );
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp expires = rs.getTimestamp("expires_at");
                boolean permanent = rs.getBoolean("permanent");

                if (permanent) {
                    rs.close();
                    ps.close();
                    return true;
                }

                if (expires != null && expires.getTime() > System.currentTimeMillis()) {
                    rs.close();
                    ps.close();
                    return true;
                } else if (expires != null) {
                    unbanPlayer(uuid, "System");
                }
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Ban-Check", e);
        }
        return false;
    }

    public String getBanReason(UUID uuid) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT reason, expires_at, permanent FROM bans WHERE uuid = ? AND active = TRUE ORDER BY banned_at DESC LIMIT 1"
            );
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String reason = rs.getString("reason");
                Timestamp expires = rs.getTimestamp("expires_at");
                boolean permanent = rs.getBoolean("permanent");

                String message = "§cDu bist gebannt!\n§7Grund: §f" + reason;

                if (!permanent && expires != null) {
                    long remaining = expires.getTime() - System.currentTimeMillis();
                    message += "\n§7Verbleibend: §f" + TimeUtils.formatDuration(remaining);
                } else {
                    message += "\n§7Dauer: §cPermanent";
                }

                rs.close();
                ps.close();
                return message;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Abrufen des Ban-Grundes", e);
        }
        return "§cDu bist gebannt!";
    }

    public int getTotalBans(UUID uuid) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) as count FROM bans WHERE uuid = ?"
            );
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                rs.close();
                ps.close();
                return count;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Abrufen der Ban-Anzahl", e);
        }
        return 0;
    }

    public UUID getUuidByName(String playerName) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT uuid FROM bans WHERE player_name = ? ORDER BY banned_at DESC LIMIT 1"
            );
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String uuidString = rs.getString("uuid");
                rs.close();
                ps.close();
                return UUID.fromString(uuidString);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Abrufen der UUID für " + playerName, e);
        }
        return null;
    }
}