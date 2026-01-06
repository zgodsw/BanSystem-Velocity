package de.bansystem.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== CHAT LISTENER ====================
public class ChatListener {

    private final BanSystem plugin;

    public ChatListener(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (plugin.getMuteManager().isMuted(uuid)) {
            event.getPlayer().sendMessage(Component.text("§cDu bist gemutet und kannst nicht schreiben!"));
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }
}
