package de.bansystem.manager;

import de.bansystem.BanSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

// ==================== REPORT MANAGER ====================
public class ReportManager {

    private final BanSystem plugin;

    public ReportManager(BanSystem plugin) {
        this.plugin = plugin;
    }

    public void createReport(UUID reportedUuid, String reportedName, UUID reporterUuid, String reporterName, String reason) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO reports (reported_uuid, reported_name, reporter_uuid, reporter_name, reason) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setString(1, reportedUuid.toString());
            ps.setString(2, reportedName);
            ps.setString(3, reporterUuid.toString());
            ps.setString(4, reporterName);
            ps.setString(5, reason);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Erstellen des Reports", e);
        }
    }

    public void updateReportStatus(int reportId, String status, String handledBy) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE reports SET status = ?, handled_by = ?, handled_at = NOW() WHERE id = ?"
            );
            ps.setString(1, status);
            ps.setString(2, handledBy);
            ps.setInt(3, reportId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().error("Fehler beim Aktualisieren des Reports", e);
        }
    }

    public int getTotalReports(UUID uuid) {
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) as count FROM reports WHERE reported_uuid = ?"
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
            plugin.getLogger().error("Fehler beim Abrufen der Report-Anzahl", e);
        }
        return 0;
    }
}
