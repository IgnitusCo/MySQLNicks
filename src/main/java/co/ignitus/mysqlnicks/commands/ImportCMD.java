package co.ignitus.mysqlnicks.commands;

import co.ignitus.mysqlnicks.MySQLNicks;
import co.ignitus.mysqlnicks.util.DataUtil;
import co.ignitus.mysqlnicks.util.MessageUtil;
import com.Zrips.CMI.CMI;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.UUID;

public class ImportCMD implements CommandExecutor {

    final private MySQLNicks mySQLNicks = MySQLNicks.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mysqlnicks.importnicks")) {
            sender.sendMessage(MessageUtil.getMessage("importnicks.no-permission"));
            return true;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("CMI")) {
            String[] replace = new String[]{"%plugin%", "CMI"};
            HashMap<UUID, String> nicknames = new HashMap<>();
            sender.sendMessage(MessageUtil.getMessage("importnicks.starting", replace));
            Bukkit.getScheduler().runTaskAsynchronously(mySQLNicks, () -> {
                CMI.getInstance().getPlayerManager().getAllUsers().forEach((uuid, user) -> {
                    String nickname = user.getNickName();
                    if (nickname == null || nickname.isEmpty())
                        return;
                    nicknames.put(uuid, nickname);
                });
                if (DataUtil.updateMultipleNicknames(nicknames))
                    sender.sendMessage(MessageUtil.getMessage("importnicks.finished", replace));
                else
                    sender.sendMessage(MessageUtil.getMessage("importnicks.error", replace));
            });
            return true;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            String[] replace = new String[]{"%plugin%", "Essentials"};
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            if (essentials == null) {
                sender.sendMessage(MessageUtil.getMessage("importnicks.error", replace));
                return true;
            }

            HashMap<UUID, String> nicknames = new HashMap<>();
            sender.sendMessage(MessageUtil.getMessage("importnicks.starting", replace));
            Bukkit.getScheduler().runTaskAsynchronously(mySQLNicks, () -> {
                essentials.getUserMap().getAllUniqueUsers().forEach(uuid -> {
                    User user = essentials.getUser(uuid);
                    if (user == null)
                        return;
                    String nickname = user.getNickname();
                    if (nickname == null || nickname.isEmpty())
                        return;
                    nicknames.put(uuid, nickname);
                });
                if (DataUtil.updateMultipleNicknames(nicknames))
                    sender.sendMessage(MessageUtil.getMessage("importnicks.finished", replace));
                else
                    sender.sendMessage(MessageUtil.getMessage("importnicks.error", replace));
            });
            return true;
        }
        sender.sendMessage(MessageUtil.getMessage("importnicks.no-dependency"));
        return true;
    }
}
