package de.bansystem.utils;

public class TimeUtils {

    /**
     * Parst eine Zeitangabe (z.B. "1h", "2d", "3w", "1M", "perm") in Millisekunden
     */
    public static long parseDuration(String input) {
        if (input.equalsIgnoreCase("perm")) {
            return -1;
        }

        try {
            int amount = Integer.parseInt(input.substring(0, input.length() - 1));
            char unit = input.charAt(input.length() - 1);

            switch (unit) {
                case 'h': return amount * 3600000L;        // Stunden
                case 'd': return amount * 86400000L;       // Tage
                case 'w': return amount * 604800000L;      // Wochen
                case 'M': return amount * 2592000000L;     // Monate (30 Tage)
                default: return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Formatiert eine Zeitdauer in Millisekunden in einen lesbaren String
     */
    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " Tag" + (days > 1 ? "e" : "");
        } else if (hours > 0) {
            return hours + " Stunde" + (hours > 1 ? "n" : "");
        } else if (minutes > 0) {
            return minutes + " Minute" + (minutes > 1 ? "n" : "");
        } else {
            return seconds + " Sekunde" + (seconds > 1 ? "n" : "");
        }
    }
}