package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import de.bansystem.manager.PunishmentTemplateManager;
import de.bansystem.utils.TimeUtils;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== MUTE COMMAND ====================
public class MuteCommand implements SimpleCommand {

    private final BanSystem plugin;

    public MuteCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("bansystem.mute")) {
            invocation.source().sendMessage(Component.text("§cDu hast keine Berechtigung dafür!"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("§cVerwendung: /mute <Spieler> <Grund|ID> [Zeit]"));
            invocation.source().sendMessage(Component.text("§7Oder: /mute <Spieler> <ID>"));
            invocation.source().sendMessage(Component.text("§7IDs: /muteids für Liste"));
            return;
        }

        String targetName = args[0];
        Player target = plugin.getServer().getPlayer(targetName).orElse(null);

        if (target == null) {
            invocation.source().sendMessage(Component.text("§cDieser Spieler ist nicht online!"));
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String mutedBy = invocation.source() instanceof Player ?
                ((Player) invocation.source()).getUsername() : "Console";

        // Prüfe ob zweites Argument eine Template-ID ist
        try {
            int templateId = Integer.parseInt(args[1]);
            PunishmentTemplateManager.PunishmentTemplate template =
                    plugin.getPunishmentTemplateManager().getMuteTemplate(templateId);

            if (template == null) {
                invocation.source().sendMessage(Component.text("§cUngültige Mute-ID! Nutze /muteids für eine Liste."));
                return;
            }

            // Mute mit Template
            plugin.getMuteManager().mutePlayer(targetUuid, targetName, template.getReason(),
                    mutedBy, template.getDuration());

            if (template.isPermanent()) {
                invocation.source().sendMessage(Component.text("§a" + targetName + " wurde permanent gemutet!"));
                invocation.source().sendMessage(Component.text("§7Grund: §f" + template.getReason()));
            } else {
                invocation.source().sendMessage(Component.text("§a" + targetName + " wurde für " +
                        template.getDurationString() + " gemutet!"));
                invocation.source().sendMessage(Component.text("§7Grund: §f" + template.getReason()));
            }

            target.sendMessage(Component.text(template.getDisplayMessage()));
            return;

        } catch (NumberFormatException e) {
            // Kein Template - normaler Mute mit Grund
        }

        // Normaler Mute-Prozess
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

        plugin.getMuteManager().mutePlayer(targetUuid, targetName, reason, mutedBy, duration);
        invocation.source().sendMessage(Component.text("§a" + targetName + " wurde gemutet!"));

        target.sendMessage(Component.text("§cDu wurdest gemutet!\n§7Grund: §f" + reason));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.mute");
    }
}

