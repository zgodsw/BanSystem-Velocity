package de.bansystem.manager;

import de.bansystem.BanSystem;
import de.bansystem.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class PunishmentTemplateManager {

    private final BanSystem plugin;
    private final Map<Integer, PunishmentTemplate> banTemplates = new HashMap<>();
    private final Map<Integer, PunishmentTemplate> muteTemplates = new HashMap<>();

    public PunishmentTemplateManager(BanSystem plugin) {
        this.plugin = plugin;
        loadTemplates();
    }

    private void loadTemplates() {
        // ==================== BAN TEMPLATES ====================

        // 1. Cheating / Hacking
        banTemplates.put(1, new PunishmentTemplate(
                "Cheating / Hacking (1. Verstoß)",
                TimeUtils.parseDuration("30d"),
                "§cCheating / Hacking ist verboten!\n§7Du wurdest für 30 Tage gebannt.\n§71. Verstoß"
        ));

        banTemplates.put(11, new PunishmentTemplate(
                "Cheating / Hacking (2. Verstoß)",
                -1, // Permanent
                "§cCheating / Hacking ist verboten!\n§7Du wurdest permanent gebannt.\n§72. Verstoß"
        ));

        // 2. Ban Evasion
        banTemplates.put(2, new PunishmentTemplate(
                "Ban Evasion",
                -1, // Permanent
                "§cBan Umgehung ist verboten!\n§7Du wurdest permanent gebannt."
        ));

        // 3. Bugusing
        banTemplates.put(3, new PunishmentTemplate(
                "Bugusing",
                TimeUtils.parseDuration("7d"),
                "§cBugusing ist verboten!\n§7Du wurdest für 7 Tage gebannt."
        ));

        // 4. Scamming
        banTemplates.put(4, new PunishmentTemplate(
                "Scamming",
                TimeUtils.parseDuration("12h"),
                "§cScamming ist verboten!\n§7Du wurdest für 12 Stunden gebannt."
        ));

        // 5. Teaming
        banTemplates.put(5, new PunishmentTemplate(
                "Teaming",
                TimeUtils.parseDuration("3d"),
                "§cTeaming ist verboten!\n§7Du wurdest für 3 Tage gebannt."
        ));

        // 6. Imitieren von Teammitgliedern
        banTemplates.put(6, new PunishmentTemplate(
                "Imitieren von Teammitgliedern",
                TimeUtils.parseDuration("1h"),
                "§cDas Imitieren von Teammitgliedern ist verboten!\n§7Du wurdest für 1 Stunde gebannt."
        ));

        // 7. Leaking / Doxing
        banTemplates.put(7, new PunishmentTemplate(
                "Leaking / Doxing",
                -1, // Permanent
                "§cLeaking / Doxing ist verboten!\n§7Du wurdest permanent gebannt."
        ));

        // ==================== MUTE TEMPLATES ====================

        // 1. Beleidigungen
        muteTemplates.put(1, new PunishmentTemplate(
                "Beleidigungen",
                TimeUtils.parseDuration("1d"),
                "§cBeleidigungen sind verboten!\n§7Du wurdest für 1 Tag gemutet."
        ));

        // 2. Spamming
        muteTemplates.put(2, new PunishmentTemplate(
                "Spamming",
                TimeUtils.parseDuration("1h"),
                "§cSpamming ist verboten!\n§7Du wurdest für 1 Stunde gemutet."
        ));

        // 3. Werbung
        muteTemplates.put(3, new PunishmentTemplate(
                "Werbung",
                TimeUtils.parseDuration("12h"),
                "§cWerbung ist verboten!\n§7Du wurdest für 12 Stunden gemutet."
        ));

        // 4. Rassistische, sexistische oder diskriminierende Aussagen (1. Verstoß)
        muteTemplates.put(4, new PunishmentTemplate(
                "Rassistische / Diskriminierende Aussagen (1. Verstoß)",
                TimeUtils.parseDuration("90d"),
                "§cRassismus und Diskriminierung sind verboten!\n§7Du wurdest für 90 Tage gemutet.\n§71. Verstoß"
        ));

        muteTemplates.put(14, new PunishmentTemplate(
                "Rassistische / Diskriminierende Aussagen (2. Verstoß)",
                -1, // Permanent
                "§cRassismus und Diskriminierung sind verboten!\n§7Du wurdest permanent gemutet.\n§72. Verstoß"
        ));

        // 5. Provokationen / Trolling
        muteTemplates.put(5, new PunishmentTemplate(
                "Provokationen / Trolling",
                TimeUtils.parseDuration("1d"),
                "§cProvokationen und Trolling sind verboten!\n§7Du wurdest für 1 Tag gemutet."
        ));

        // 6. Unangemessene Sprache
        muteTemplates.put(6, new PunishmentTemplate(
                "Unangemessene Sprache",
                TimeUtils.parseDuration("8h"),
                "§cUnangemessene Sprache ist verboten!\n§7Du wurdest für 8 Stunden gemutet."
        ));

        // 7. Falsche Anschuldigungen
        muteTemplates.put(7, new PunishmentTemplate(
                "Falsche Anschuldigungen",
                TimeUtils.parseDuration("30m"),
                "§cFalsche Anschuldigungen sind verboten!\n§7Du wurdest für 30 Minuten gemutet."
        ));

        // 8. Chat-Flooding (Warnung - 1 Minute)
        muteTemplates.put(8, new PunishmentTemplate(
                "Chat-Flooding (Warnung)",
                TimeUtils.parseDuration("1m"),
                "§eWARNUNG: §cChat-Flooding ist verboten!\n§7Du wurdest für 1 Minute gemutet."
        ));

        // 9. Umgehung eines Mutes
        muteTemplates.put(9, new PunishmentTemplate(
                "Umgehung eines Mutes",
                -1, // Permanent
                "§cDie Umgehung eines Mutes ist verboten!\n§7Du wurdest permanent gemutet."
        ));

        plugin.getLogger().info("Punishment Templates geladen: " + banTemplates.size() + " Bans, " + muteTemplates.size() + " Mutes");
    }

    public PunishmentTemplate getBanTemplate(int id) {
        return banTemplates.get(id);
    }

    public PunishmentTemplate getMuteTemplate(int id) {
        return muteTemplates.get(id);
    }

    public Map<Integer, PunishmentTemplate> getAllBanTemplates() {
        return new HashMap<>(banTemplates);
    }

    public Map<Integer, PunishmentTemplate> getAllMuteTemplates() {
        return new HashMap<>(muteTemplates);
    }

    public boolean isBanTemplateValid(int id) {
        return banTemplates.containsKey(id);
    }

    public boolean isMuteTemplateValid(int id) {
        return muteTemplates.containsKey(id);
    }

    // ==================== PUNISHMENT TEMPLATE CLASS ====================

    public static class PunishmentTemplate {
        private final String reason;
        private final long duration;
        private final String displayMessage;

        public PunishmentTemplate(String reason, long duration, String displayMessage) {
            this.reason = reason;
            this.duration = duration;
            this.displayMessage = displayMessage;
        }

        public String getReason() {
            return reason;
        }

        public long getDuration() {
            return duration;
        }

        public String getDisplayMessage() {
            return displayMessage;
        }

        public boolean isPermanent() {
            return duration == -1;
        }

        public String getDurationString() {
            if (duration == -1) {
                return "Permanent";
            }
            return TimeUtils.formatDuration(duration);
        }
    }
}