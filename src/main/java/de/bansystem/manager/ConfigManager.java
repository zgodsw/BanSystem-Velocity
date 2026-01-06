package de.bansystem.manager;

import de.bansystem.BanSystem;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private final BanSystem plugin;
    private ConfigurationNode config;

    public ConfigManager(BanSystem plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        try {
            Path configPath = plugin.getDataDirectory().resolve("config.yml");

            if (!Files.exists(plugin.getDataDirectory())) {
                Files.createDirectories(plugin.getDataDirectory());
            }

            if (!Files.exists(configPath)) {
                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                    }
                }
            }

            YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder()
                    .setPath(configPath)
                    .build();

            config = loader.load();
            plugin.getLogger().info("Konfiguration erfolgreich geladen!");

        } catch (IOException e) {
            plugin.getLogger().error("Fehler beim Laden der Konfiguration!", e);
        }
    }

    public String getMySQLHost() {
        return config.getNode("mysql", "host").getString("localhost");
    }

    public int getMySQLPort() {
        return config.getNode("mysql", "port").getInt(3306);
    }

    public String getMySQLDatabase() {
        return config.getNode("mysql", "database").getString("ban");
    }

    public String getMySQLUsername() {
        return config.getNode("mysql", "username").getString("admin");
    }

    public String getMySQLPassword() {
        return config.getNode("mysql", "password").getString("M8YkJ0l2U)Gd2zlZ");
    }

    public boolean isWebInterfaceEnabled() {
        return config.getNode("webinterface", "enabled").getBoolean(true);
    }

    public int getWebInterfacePort() {
        return config.getNode("webinterface", "port").getInt(8080);
    }

    public String getMessage(String path) {
        return config.getNode("messages", path).getString("§cNachricht nicht gefunden: " + path);
    }
}