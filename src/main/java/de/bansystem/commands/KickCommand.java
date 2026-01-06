package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== KICK COMMAND ====================
public class KickCommand implements SimpleCommand {

    private final BanSystem plugin;

    public KickCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("bansystem.kick")) {
            invocation.source().sendMessage(Component.text("§cDu hast keine Berechtigung dafür!"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("§cVerwendung: /kick <Spieler> <Grund>"));
            return;
        }

        String targetName = args[0];
        Player target = plugin.getServer().getPlayer(targetName).orElse(null);

        if (target == null) {
            invocation.source().sendMessage(Component.text("§cSpieler ist nicht online!"));
            return;
        }

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        UUID targetUuid = target.getUniqueId();
        String kickedBy = invocation.source() instanceof Player ?
                ((Player) invocation.source()).getUsername() : "Console";

        plugin.getKickManager().kickPlayer(targetUuid, targetName, reason, kickedBy);
        invocation.source().sendMessage(Component.text("§a" + targetName + " wurde gekickt!"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.kick");
    }
}
