package co.ignitus.mysqlnicks.util;

import co.ignitus.mysqlnicks.MySQLNicks;
import org.bukkit.ChatColor;

public class MessageUtil {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getMessage(String path, String... replace) {
        String message = MySQLNicks.getInstance().getConfig().getString("messages." + path, "&cUnknown message.");
        for (int i = 0; i < replace.length; i+= 2)
            message = message.replace(replace[i], replace[i+1]);
        return format(message);
    }
}
