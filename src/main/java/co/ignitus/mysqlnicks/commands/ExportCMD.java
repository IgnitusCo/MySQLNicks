package co.ignitus.mysqlnicks.commands;

import co.ignitus.mysqlnicks.MySQLNicks;
import co.ignitus.mysqlnicks.util.DataUtil;
import co.ignitus.mysqlnicks.util.MessageUtil;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ExportCMD implements CommandExecutor {

    final private MySQLNicks mySQLNicks = MySQLNicks.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //If the player doesn't have the appropriate permission, sends them the error message
        if (!sender.hasPermission("mysqlnicks.exportnicks")) {
            sender.sendMessage(MessageUtil.getMessage("exportnicks.no-permission"));
            return true;
        }

        //Checks if CMI is enabled on the server
        if (Bukkit.getPluginManager().isPluginEnabled("CMI")) {
            String[] replace = new String[]{"%plugin%", "CMI"};
            sender.sendMessage(MessageUtil.getMessage("exportnicks.starting", replace));
            Bukkit.getScheduler().runTaskAsynchronously(mySQLNicks, () -> {
                //Loops through all the nicknames in the database and hooks into CMI to change a player's nickname in the database
                DataUtil.getSavedNicknames().forEach((uuid, nickname) -> {
                    CMIUser user = CMI.getInstance().getPlayerManager().getUser(uuid);
                    user.setNickName(nickname, true);
                });
                sender.sendMessage(MessageUtil.getMessage("exportnicks.finished", replace));
            });
            return true;
        }

        //Checks if Essentials is enabled.
        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            String[] replace = new String[]{"%plugin%", "Essentials"};
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            if (essentials == null) {
                sender.sendMessage(MessageUtil.getMessage("exportnicks.error", replace));
                return true;
            }
            sender.sendMessage(MessageUtil.getMessage("exportnicks.starting", replace));
            Bukkit.getScheduler().runTaskAsynchronously(mySQLNicks, () -> {
                //Loops through all the nicknames in the database and hooks into Essentials to change a player's nickname in the database
                DataUtil.getSavedNicknames().forEach((uuid, nickname) -> {
                    User user = essentials.getUser(uuid);
                    if (user == null)
                        return;
                    user.setNickname(nickname);
                });
                sender.sendMessage(MessageUtil.getMessage("exportnicks.finished", replace));
            });
            return true;
        }

        sender.sendMessage(MessageUtil.getMessage("exportnicks.no-dependency"));
        return true;
    }
}
