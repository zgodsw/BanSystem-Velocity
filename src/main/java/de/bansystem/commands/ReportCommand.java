package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.sql.*;
import java.util.UUID;

// ==================== REPORT COMMAND ====================
public class ReportCommand implements SimpleCommand {

    private final BanSystem plugin;

    public ReportCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Component.text("§cNur Spieler können diesen Befehl nutzen!"));
            return;
        }

        Player reporter = (Player) invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("§cVerwendung: /report <Spieler> <Grund>"));
            return;
        }

        String reportedName = args[0];
        Player reported = plugin.getServer().getPlayer(reportedName).orElse(null);

        if (reported == null) {
            invocation.source().sendMessage(Component.text("§cSpieler nicht gefunden!"));
            return;
        }

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        plugin.getReportManager().createReport(
                reported.getUniqueId(),
                reportedName,
                reporter.getUniqueId(),
                reporter.getUsername(),
                reason
        );

        invocation.source().sendMessage(Component.text("§aDein Report wurde erstellt!"));

        // Benachrichtige Team
        for (Player online : plugin.getServer().getAllPlayers()) {
            if (online.hasPermission("bansystem.notify")) {
                online.sendMessage(Component.text("§c[Report] §f" + reporter.getUsername() +
                        " §7hat §f" + reportedName + " §7gemeldet!"));
            }
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // Jeder kann reporten
    }
}

