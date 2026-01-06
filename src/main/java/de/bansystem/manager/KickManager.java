package de.bansystem.manager;

import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.sql.*;
import java.util.UUID;

// ==================== KICK MANAGER ====================
public class KickManager {

    private final BanSystem plugin;

    public KickManager(BanSystem plugin) {
        this.plugin = plugin;
    }

    public void kickPlayer(UUID uuid, String playerName, String reason, String kickedBy) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO kicks (uuid, player_name, reason, kicked_by) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, uuid.toString());
            ps.setString(2, playerName);
            ps.setString(3, reason);
            ps.setString(4, kickedBy);
            ps.executeUpdate();
            ps.close();

            plugin.getServer().getPlayer(uuid).ifPresent(player -> {
                player.disconnect(Component.text("§cDu wurdest gekickt!\n§7Grund: §f" + reason));
            });

        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Kicken von " + playerName, e);
        }
    }

    public int getTotalKicks(UUID uuid) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) as count FROM kicks WHERE uuid = ?"
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
            plugin.getLogger().error("Fehler beim Abrufen der Kick-Anzahl", e);
        }
        return 0;
    }
}

