package co.ignitus.mysqlnicks.util;

import co.ignitus.mysqlnicks.MySQLNicks;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static co.ignitus.mysqlnicks.util.MessageUtil.format;
import static co.ignitus.mysqlnicks.util.MessageUtil.stripColor;

public class PlaceholderUtil extends PlaceholderExpansion {

    private final MySQLNicks mySQLNicks;

    public PlaceholderUtil(MySQLNicks mySQLNicks) {
        this.mySQLNicks = mySQLNicks;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Ignitus Co.";
    }

    @Override
    public String getIdentifier() {
        return "mysqlnicks";
    }

    @Override
    public String getVersion() {
        return mySQLNicks.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equalsIgnoreCase("nickname")) {
            String nickname = DataUtil.getNickname(player.getUniqueId());
            if (nickname == null)
                return player.getName();
            return format(mySQLNicks.getConfig().getString("nickname-prefix", "") + nickname);
        }
        if (identifier.equalsIgnoreCase("nocolor") || identifier.equalsIgnoreCase("nocolour")) {
            String nickname = DataUtil.getNickname(player.getUniqueId());
            if (nickname == null)
                return player.getName();
            String message = mySQLNicks.getConfig().getString("nickname-prefix", "") + nickname;
            return player.hasPermission("mysqlnicks.bypass.nocolor") ? format(message) : stripColor(message);
        }
        return null;
    }

}
