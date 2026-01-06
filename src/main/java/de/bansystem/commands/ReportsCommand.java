package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// ==================== REPORTS COMMAND ====================
public class ReportsCommand implements SimpleCommand {

    private final BanSystem plugin;

    public ReportsCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Component.text("§cNur Spieler können diesen Befehl nutzen!"));
            return;
        }

        if (!invocation.source().hasPermission("bansystem.reports")) {
            invocation.source().sendMessage(Component.text("§cDu hast keine Berechtigung dafür!"));
            return;
        }

        Player player = (Player) invocation.source();

        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM reports WHERE status = 'OPEN' ORDER BY reported_at DESC LIMIT 10"
            );
            ResultSet rs = ps.executeQuery();

            player.sendMessage(Component.text("§8§m-------------------"));
            player.sendMessage(Component.text("§6§lOffene Reports"));
            player.sendMessage(Component.text("§8§m-------------------"));

            int count = 0;
            while (rs.next()) {
                count++;
                int id = rs.getInt("id");
                String reportedName = rs.getString("reported_name");
                String reporterName = rs.getString("reporter_name");
                String reason = rs.getString("reason");

                player.sendMessage(Component.text(""));
                player.sendMessage(Component.text("§7ID: §e#" + id));
                player.sendMessage(Component.text("§7Spieler: §c" + reportedName));
                player.sendMessage(Component.text("§7Reporter: §f" + reporterName));
                player.sendMessage(Component.text("§7Grund: §f" + reason));
                player.sendMessage(Component.text("§7Status: §aOFFEN"));
            }

            if (count == 0) {
                player.sendMessage(Component.text("§aKeine offenen Reports vorhanden!"));
            }

            player.sendMessage(Component.text("§8§m-------------------"));
            rs.close();
            ps.close();

        } catch (SQLException e) {
            player.sendMessage(Component.text("§cFehler beim Laden der Reports!"));
            plugin.getLogger().error("Fehler beim Laden der Reports", e);
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.reports");
    }
}
