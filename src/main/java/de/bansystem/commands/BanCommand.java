package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import de.bansystem.manager.PunishmentTemplateManager;
import de.bansystem.utils.TimeUtils;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== BAN COMMAND ====================
public class BanCommand implements SimpleCommand {

    private final BanSystem plugin;

    public BanCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("bansystem.ban")) {
            invocation.source().sendMessage(Component.text("Â§cDu hast keine Berechtigung dafuer!"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("§cVerwendung: /ban <Spieler> <Grund|ID> [Zeit]"));
            invocation.source().sendMessage(Component.text("§7Zeit: 1h, 1d, 1w, 1M, perm"));
            invocation.source().sendMessage(Component.text("§7Oder: /ban <Spieler> <ID>"));
            invocation.source().sendMessage(Component.text("§7IDs: /banids fuer IDListe"));
            return;
        }

        String targetName = args[0];

        // Spieler muss online sein
        Player target = plugin.getServer().getPlayer(targetName).orElse(null);
        if (target == null) {
            invocation.source().sendMessage(Component.text("Â§cDieser Spieler ist nicht online!"));
            invocation.source().sendMessage(Component.text("Â§7Hinweis: Der Spieler muss online sein, um gebannt zu werden."));
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String bannedBy = invocation.source() instanceof Player ?
                ((Player) invocation.source()).getUsername() : "Console";

        // PrÃ¼fe ob zweites Argument eine Template-ID ist
        try {
            int templateId = Integer.parseInt(args[1]);
            PunishmentTemplateManager.PunishmentTemplate template =
                    plugin.getPunishmentTemplateManager().getBanTemplate(templateId);

            if (template == null) {
                invocation.source().sendMessage(Component.text("Â§cUngültige Ban-ID! Nutze /banids für eine Liste."));
                return;
            }

            // Banne mit Template
            plugin.getBanManager().banPlayer(targetUuid, targetName, template.getReason(),
                    bannedBy, template.getDuration());

            if (template.isPermanent()) {
                invocation.source().sendMessage(Component.text("Â§a" + targetName + " wurde permanent gebannt!"));
                invocation.source().sendMessage(Component.text("Â§7Grund: Â§f" + template.getReason()));
            } else {
                invocation.source().sendMessage(Component.text("Â§a" + targetName + " wurde fÃ¼r " +
                        template.getDurationString() + " gebannt!"));
                invocation.source().sendMessage(Component.text("Â§7Grund: Â§f" + template.getReason()));
            }

            return;

        } catch (NumberFormatException e) {
            // Kein Template - normaler Ban mit Grund
        }

        // Normaler Ban-Prozess
        StringBuilder reasonBuilder = new StringBuilder();
        long duration = -1;

        String lastArg = args[args.length - 1];
        if (lastArg.matches("\\d+[hdwMm]") || lastArg.equalsIgnoreCase("perm")) {
            duration = TimeUtils.parseDuration(lastArg);
            for (int i = 1; i < args.length - 1; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
        } else {
            for (int i = 1; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
        }

        String reason = reasonBuilder.toString().trim();

        plugin.getBanManager().banPlayer(targetUuid, targetName, reason, bannedBy, duration);

        if (duration == -1) {
            invocation.source().sendMessage(Component.text("Â§a" + targetName + " wurde permanent gebannt!"));
        } else {
            invocation.source().sendMessage(Component.text("Â§a" + targetName + " wurde fÃ¼r " +
                    TimeUtils.formatDuration(duration) + " gebannt!"));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.ban");
    }
}

