package co.ignitus.mysqlnicks.util;

import co.ignitus.mysqlnicks.MySQLNicks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    private static final String HEX_REGEX = "\\{#([A-Fa-f0-9]{6})}|#([A-Fa-f0-9]{6})";

    public static String formatLegacy(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String formatHex(String message) {
        if (Bukkit.getVersion().contains("1.16")) {
            Pattern pattern = Pattern.compile(HEX_REGEX);
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                final String match = matcher.group();
                final String colour = match
                        .replace("{", "")
                        .replace("}", "");
                message = message.replace(match, net.md_5.bungee.api.ChatColor.of(colour) + "");
            }
        } else
            message = message.replaceAll(HEX_REGEX, "");
        return message;
    }

    public static String format(String message) {
        return formatLegacy(formatHex(message));
    }

    public static String stripLegacy(String message) {
        return ChatColor.stripColor(formatLegacy(message));
    }

    public static String stripHex(String message) {
        return message.replaceAll(HEX_REGEX, "");
    }

    public static String stripColor(String message) {
        return ChatColor.stripColor(formatLegacy(message))
                .replaceAll(HEX_REGEX, "");
    }

    public static String getMessage(String path, String... replace) {
        String message = MySQLNicks.getInstance().getConfig().getString("messages." + path, "&cUnknown message. Please update your config.yml");
        for (int i = 0; i < replace.length; i += 2)
            message = message.replace(replace[i], replace[i + 1]);
        return format(message);
    }
}
