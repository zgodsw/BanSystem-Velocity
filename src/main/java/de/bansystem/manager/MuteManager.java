package de.bansystem.manager;

import de.bansystem.BanSystem;

import java.sql.*;
import java.util.UUID;

public class MuteManager {

    private final BanSystem plugin;

    public MuteManager(BanSystem plugin) {
        this.plugin = plugin;
    }

    public void mutePlayer(UUID uuid, String playerName, String reason, String mutedBy, long duration) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO mutes (uuid, player_name, reason, muted_by, expires_at, permanent) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, uuid.toString());
            ps.setString(2, playerName);
            ps.setString(3, reason);
            ps.setString(4, mutedBy);

            if (duration == -1) {
                ps.setNull(5, Types.TIMESTAMP);
                ps.setBoolean(6, true);
            } else {
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis() + duration));
                ps.setBoolean(6, false);
            }

            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Muten von " + playerName, e);
        }
    }

    public void unmutePlayer(UUID uuid, String unmutedBy) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE mutes SET active = FALSE, unmuted_by = ?, unmuted_at = NOW() WHERE uuid = ? AND active = TRUE"
            );
            ps.setString(1, unmutedBy);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Entmuten", e);
        }
    }

    public boolean isMuted(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM mutes WHERE uuid = ? AND active = TRUE ORDER BY muted_at DESC LIMIT 1"
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
                    unmutePlayer(uuid, "System");
                }
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Mute-Check", e);
        }
        return false;
    }
}