package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== UNMUTE COMMAND ====================
public class UnmuteCommand implements SimpleCommand {

    private final BanSystem plugin;

    public UnmuteCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("bansystem.unmute")) {
            invocation.source().sendMessage(Component.text("§cDu hast keine Berechtigung dafür!"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 1) {
            invocation.source().sendMessage(Component.text("§cVerwendung: /unmute <Spieler>"));
            return;
        }

        String targetName = args[0];
        Player target = plugin.getServer().getPlayer(targetName).orElse(null);

        if (target == null) {
            invocation.source().sendMessage(Component.text("§cDieser Spieler ist nicht online!"));
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String unmutedBy = invocation.source() instanceof Player ?
                ((Player) invocation.source()).getUsername() : "Console";

        plugin.getMuteManager().unmutePlayer(targetUuid, unmutedBy);
        invocation.source().sendMessage(Component.text("§a" + targetName + " wurde entmutet!"));
        target.sendMessage(Component.text("§aDu wurdest entmutet!"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.unmute");
    }
}
