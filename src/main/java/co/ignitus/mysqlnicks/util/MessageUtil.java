package co.ignitus.mysqlnicks.util;

import co.ignitus.mysqlnicks.MySQLNicks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    public static String format(String message) {
        if (Bukkit.getVersion().contains("1.16")) {
            Pattern pattern = Pattern.compile("\\{?#([A-Fa-f0-9]{6})}?");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String result = "#" + matcher.group(1);
                message = message.replace(matcher.group(0), net.md_5.bungee.api.ChatColor.of(result) + "");
            }
        } else
            message = message.replaceAll("\\{?#([A-Fa-f0-9]{6})}?", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String stripColor(String message) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message))
                .replaceAll("\\{?#([A-Fa-f0-9]{6})}?", "");
    }

    public static String getMessage(String path, String... replace) {
        String message = MySQLNicks.getInstance().getConfig().getString("messages." + path, "&cUnknown message. Please update your config.yml");
        for (int i = 0; i < replace.length; i += 2)
            message = message.replace(replace[i], replace[i + 1]);
        return format(message);
    }
}
