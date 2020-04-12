package co.ignitus.mysqlnicks.util;

import co.ignitus.mysqlnicks.MySQLNicks;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderUtil extends PlaceholderExpansion {

    private MySQLNicks mySQLNicks;

    public PlaceholderUtil(MySQLNicks mySQLNicks) {
        this.mySQLNicks = mySQLNicks;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor(){
        return "Ignitus Co.";
    }

    @Override
    public String getIdentifier(){
        return "mysqlnicks";
    }

    @Override
    public String getVersion(){
        return mySQLNicks.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if(!identifier.equalsIgnoreCase("nickname"))
            return null;
        String nickname = DataUtil.getNickname(player.getUniqueId());
        if(nickname == null)
            return player.getName();
        return MessageUtil.format(mySQLNicks.getConfig().getString("nickname-prefix", "") + nickname);
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(!identifier.equalsIgnoreCase("nickname"))
            return null;
        String nickname = DataUtil.getNickname(player.getUniqueId());
        if(nickname == null)
            return player.getName();
        return MessageUtil.format(mySQLNicks.getConfig().getString("nickname-prefix", "") + nickname);
    }
}
