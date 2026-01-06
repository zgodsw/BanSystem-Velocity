package de.bansystem.manager;

import de.bansystem.BanSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

// ==================== NOTE MANAGER ====================
public class NoteManager {

    private final BanSystem plugin;

    public NoteManager(BanSystem plugin) {
        this.plugin = plugin;
    }

    public void addNote(UUID uuid, String playerName, String note, String createdBy) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO notes (uuid, player_name, note, created_by) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, uuid.toString());
            ps.setString(2, playerName);
            ps.setString(3, note);
            ps.setString(4, createdBy);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Hinzufügen der Notiz", e);
        }
    }
}
