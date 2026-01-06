package de.bansystem;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.bansystem.commands.*;
import de.bansystem.database.DatabaseManager;
import de.bansystem.listeners.ChatListener;
import de.bansystem.listeners.LoginListener;
import de.bansystem.manager.*;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "bansystem",
        name = "BanSystem",
        version = "2.0",
        description = "Professional Velocity Ban System",
        authors = {"zgodsw"}
)
public class BanSystem {

    private static BanSystem instance;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private BanManager banManager;
    private MuteManager muteManager;
    private KickManager kickManager;
    private ReportManager reportManager;
    private NoteManager noteManager;
    private PunishmentTemplateManager punishmentTemplateManager;

    @Inject
    public BanSystem(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("=================================");
        logger.info("  BanSystem v2.0 wird geladen...");
        logger.info("=================================");

        // Konfiguration laden
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Datenbank initialisieren
        databaseManager = new DatabaseManager(this);
        if (!databaseManager.connect()) {
            logger.error("Datenbankverbindung fehlgeschlagen! Plugin wird deaktiviert.");
            return;
        }

        // Manager initialisieren
        banManager = new BanManager(this);
        muteManager = new MuteManager(this);
        kickManager = new KickManager(this);
        reportManager = new ReportManager(this);
        noteManager = new NoteManager(this);
        punishmentTemplateManager = new PunishmentTemplateManager(this);

        // Commands registrieren
        registerCommands();

        // Listener registrieren
        registerListeners();

        logger.info("=================================");
        logger.info("  BanSystem erfolgreich geladen!");
        logger.info("=================================");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        logger.info("BanSystem wurde gestoppt!");
    }

    private void registerCommands() {
        server.getCommandManager().register("ban", new BanCommand(this));
        server.getCommandManager().register("unban", new UnbanCommand(this));
        server.getCommandManager().register("mute", new MuteCommand(this));
        server.getCommandManager().register("unmute", new UnmuteCommand(this));
        server.getCommandManager().register("kick", new KickCommand(this));
        server.getCommandManager().register("report", new ReportCommand(this));
        server.getCommandManager().register("reports", new ReportsCommand(this));
        server.getCommandManager().register("notes", new NotesCommand(this));
        server.getCommandManager().register("check", new CheckCommand(this));
        server.getCommandManager().register("banids", new BanIDsCommand(this));
        server.getCommandManager().register("muteids", new MuteIDsCommand(this));
    }

    private void registerListeners() {
        server.getEventManager().register(this, new LoginListener(this));
        server.getEventManager().register(this, new ChatListener(this));
    }

    // Getters
    public static BanSystem getInstance() {
        return instance;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public KickManager getKickManager() {
        return kickManager;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public NoteManager getNoteManager() {
        return noteManager;
    }

    public PunishmentTemplateManager getPunishmentTemplateManager() {
        return punishmentTemplateManager;
    }
}