package de.bansystem.listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import de.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.util.UUID;

// ==================== LOGIN LISTENER ====================
public class LoginListener {

    private final BanSystem plugin;

    public LoginListener(BanSystem plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String playerName = event.getPlayer().getUsername();

        plugin.getLogger().info("Login-Versuch: " + playerName + " (UUID: " + uuid + ")");

        if (plugin.getBanManager().isBanned(uuid)) {
            String banMessage = plugin.getBanManager().getBanReason(uuid);
            plugin.getLogger().info("Spieler " + playerName + " ist gebannt - Verbindung wird abgelehnt");

            event.setResult(ResultedEvent.ComponentResult.denied(
                    Component.text(banMessage)
            ));
        } else {
            plugin.getLogger().info("Spieler " + playerName + " ist nicht gebannt - Login erlaubt");
        }
    }
}

