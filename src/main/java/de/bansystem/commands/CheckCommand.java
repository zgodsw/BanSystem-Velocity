package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== CHECK COMMAND ====================
public class CheckCommand implements SimpleCommand {

    private final BanSystem plugin;

    public CheckCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("bansystem.check")) {
            invocation.source().sendMessage(Component.text("§cDu hast keine Berechtigung dafür!"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 1) {
            invocation.source().sendMessage(Component.text("§cVerwendung: /check <Spieler>"));
            return;
        }

        String targetName = args[0];
        Player target = plugin.getServer().getPlayer(targetName).orElse(null);

        if (target == null) {
            invocation.source().sendMessage(Component.text("§cDieser Spieler ist nicht online!"));
            return;
        }

        UUID targetUuid = target.getUniqueId();

        invocation.source().sendMessage(Component.text("§8§m-------------------"));
        invocation.source().sendMessage(Component.text("§6Info über §e" + targetName));
        invocation.source().sendMessage(Component.text("§7UUID: §f" + targetUuid));
        invocation.source().sendMessage(Component.text("§7Gebannt: " +
                (plugin.getBanManager().isBanned(targetUuid) ? "§cJa" : "§aNein")));
        invocation.source().sendMessage(Component.text("§7Gemutet: " +
                (plugin.getMuteManager().isMuted(targetUuid) ? "§cJa" : "§aNein")));

        int bans = plugin.getBanManager().getTotalBans(targetUuid);
        int kicks = plugin.getKickManager().getTotalKicks(targetUuid);
        int reports = plugin.getReportManager().getTotalReports(targetUuid);

        invocation.source().sendMessage(Component.text("§7Bans: §f" + bans));
        invocation.source().sendMessage(Component.text("§7Kicks: §f" + kicks));
        invocation.source().sendMessage(Component.text("§7Reports: §f" + reports));
        invocation.source().sendMessage(Component.text("§8§m-------------------"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.check");
    }
}
