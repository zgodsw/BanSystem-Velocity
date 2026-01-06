package de.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== NOTES COMMAND ====================
public class NotesCommand implements SimpleCommand {

    private final BanSystem plugin;

    public NotesCommand(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("bansystem.notes")) {
            invocation.source().sendMessage(Component.text("§cDu hast keine Berechtigung dafür!"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("§cVerwendung: /notes <Spieler> <Notiz>"));
            return;
        }

        String targetName = args[0];
        Player target = plugin.getServer().getPlayer(targetName).orElse(null);

        if (target == null) {
            invocation.source().sendMessage(Component.text("§cDieser Spieler ist nicht online!"));
            return;
        }

        StringBuilder noteBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            noteBuilder.append(args[i]).append(" ");
        }
        String note = noteBuilder.toString().trim();

        UUID targetUuid = target.getUniqueId();
        String createdBy = invocation.source() instanceof Player ?
                ((Player) invocation.source()).getUsername() : "Console";

        plugin.getNoteManager().addNote(targetUuid, targetName, note, createdBy);
        invocation.source().sendMessage(Component.text("§aNotiz für " + targetName + " hinzugefügt!"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.notes");
    }
}
