package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import de.bansystem.BanSystem;
import de.bansystem.manager.PunishmentTemplateManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Map;

// ==================== BAN IDS COMMAND ====================
public class BanIDsCommand implements SimpleCommand {

    private final BanSystem plugin;

    public BanIDsCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("bansystem.ban")) {
            invocation.source().sendMessage(Component.text("§cDu hast keine Berechtigung dafür!"));
            return;
        }

        Map<Integer, PunishmentTemplateManager.PunishmentTemplate> templates =
                plugin.getPunishmentTemplateManager().getAllBanTemplates();

        invocation.source().sendMessage(Component.text(""));
        invocation.source().sendMessage(Component.text("════════════════════════════════════")
                .color(NamedTextColor.DARK_GRAY));
        invocation.source().sendMessage(Component.text("        🛡️ Ban Template IDs")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        invocation.source().sendMessage(Component.text("════════════════════════════════════")
                .color(NamedTextColor.DARK_GRAY));
        invocation.source().sendMessage(Component.text(""));

        // Sortiere nach ID
        templates.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int id = entry.getKey();
                    PunishmentTemplateManager.PunishmentTemplate template = entry.getValue();

                    invocation.source().sendMessage(
                            Component.text("  §6ID " + id + " §8│ §f" + template.getReason())
                    );
                    invocation.source().sendMessage(
                            Component.text("        §7Dauer: §e" + template.getDurationString())
                    );
                    invocation.source().sendMessage(Component.text(""));
                });

        invocation.source().sendMessage(Component.text("  §7Verwendung: §f/ban <Spieler> <ID>")
                .color(NamedTextColor.GRAY));
        invocation.source().sendMessage(Component.text("  §7Beispiel: §f/ban Spieler123 1")
                .color(NamedTextColor.GRAY));
        invocation.source().sendMessage(Component.text("════════════════════════════════════")
                .color(NamedTextColor.DARK_GRAY));
        invocation.source().sendMessage(Component.text(""));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.ban");
    }
}

