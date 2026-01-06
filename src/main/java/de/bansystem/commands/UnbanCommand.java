package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== UNBAN COMMAND ====================
public class UnbanCommand implements SimpleCommand {

    private final BanSystem plugin;

    public UnbanCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("bansystem.unban")) {
            invocation.source().sendMessage(Component.text("§cDu hast keine Berechtigung dafür!"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 1) {
            invocation.source().sendMessage(Component.text("§cVerwendung: /unban <Spieler>"));
            return;
        }

        String targetName = args[0];

        // Versuche den Spieler zu finden (online oder offline)
        plugin.getServer().getPlayer(targetName).ifPresentOrElse(
                // Spieler ist online
                player -> {
                    UUID targetUuid = player.getUniqueId();
                    String unbannedBy = invocation.source() instanceof Player ?
                            ((Player) invocation.source()).getUsername() : "Console";

                    if (!plugin.getBanManager().isBanned(targetUuid)) {
                        invocation.source().sendMessage(Component.text("§c" + targetName + " ist nicht gebannt!"));
                        return;
                    }

                    plugin.getBanManager().unbanPlayer(targetUuid, unbannedBy);
                    invocation.source().sendMessage(Component.text("§a" + targetName + " wurde entbannt!"));
                },
                // Spieler ist offline - UUID aus Datenbank holen
                () -> {
                    UUID targetUuid = plugin.getBanManager().getUuidByName(targetName);

                    if (targetUuid == null) {
                        invocation.source().sendMessage(Component.text("§cSpieler nicht gefunden!"));
                        invocation.source().sendMessage(Component.text("§7Der Spieler war noch nie auf dem Server."));
                        return;
                    }

                    if (!plugin.getBanManager().isBanned(targetUuid)) {
                        invocation.source().sendMessage(Component.text("§c" + targetName + " ist nicht gebannt!"));
                        return;
                    }

                    String unbannedBy = invocation.source() instanceof Player ?
                            ((Player) invocation.source()).getUsername() : "Console";

                    plugin.getBanManager().unbanPlayer(targetUuid, unbannedBy);
                    invocation.source().sendMessage(Component.text("§a" + targetName + " wurde entbannt!"));
                }
        );
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.unban");
    }
}