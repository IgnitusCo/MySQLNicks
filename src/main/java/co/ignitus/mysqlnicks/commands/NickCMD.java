package co.ignitus.mysqlnicks.commands;

import co.ignitus.mysqlnicks.MySQLNicks;
import co.ignitus.mysqlnicks.util.DataUtil;
import co.ignitus.mysqlnicks.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class NickCMD implements CommandExecutor {

    final MySQLNicks mySQLNicks = MySQLNicks.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getMessage("nick.insufficient-arguments"));
            return true;
        }

        if (!sender.hasPermission("mysqlnicks.nick")) {
            sender.sendMessage(MessageUtil.getMessage("nick.no-permission"));
            return true;
        }

        //The player who's nickname is been changed
        final Player target;
        String nickname;
        if (args.length == 2 && sender.hasPermission("mysqlnicks.staff")) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtil.getMessage("nick.invalid-player"));
                return true;
            }
            nickname = args[1];
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtil.getMessage("nick.no-console"));
                return true;
            }
            target = ((Player) sender);
            nickname = args[0];
        }

        final FileConfiguration config = mySQLNicks.getConfig();
        if (config.getBoolean("limit.enabled")) {
            boolean includeColors = config.getBoolean("limit.include-color");
            int length = config.getInt("limit.length");
            String input = includeColors ? nickname : ChatColor.stripColor(MessageUtil.format(nickname));
            if (input.length() > length && !sender.hasPermission("mysqlnicks.bypass.limit")) {
                sender.sendMessage(MessageUtil.getMessage("nick.exceeds-limit"));
                return true;
            }
        }

        nickname = nickname.replace("§", "&");
        if (!sender.hasPermission("mysqlnicks.nick.color")) {
            if (!sender.hasPermission("mysqlnicks.nick.color.simple") && !MessageUtil.stripLegacy(nickname).equals(nickname)) {
                sender.sendMessage(MessageUtil.getMessage("nick.no-codes"));
                return true;
            }
            if (!sender.hasPermission("mysqlnicks.nick.color.hex") && !MessageUtil.stripHex(nickname).equals(nickname)) {
                sender.sendMessage(MessageUtil.getMessage("nick.no-hex"));
                return true;
            }
        }
        if (!sender.hasPermission("mysqlnicks.nick.format") && (nickname.contains("&l") ||
                nickname.contains("&m") || nickname.contains("&n") || nickname.contains("&o") ||
                nickname.contains("&r"))) {
            sender.sendMessage(MessageUtil.getMessage("nick.no-formatting"));
            return true;
        }
        if (!sender.hasPermission("mysqlnicks.nick.magic") && nickname.contains("&k")) {
            sender.sendMessage(MessageUtil.getMessage("nick.no-magic"));
            return true;
        }

        if (nickname.equalsIgnoreCase("off"))
            nickname = null;
        final String path;
        if (sender instanceof Player) {
            path = "nick." + (target.equals(sender) ? "" : "staff.");
        } else {
            path = "nick.staff.";
        }
        if (!DataUtil.setNickname(target.getUniqueId(), nickname)) {
            sender.sendMessage(MessageUtil.getMessage(path + "error",
                    "%player%", target.getName()));
            return true;
        }
        if (nickname == null) {
            sender.sendMessage(MessageUtil.getMessage(path + "unset",
                    "%player%", target.getName()));
            return true;
        }
        sender.sendMessage(MessageUtil.getMessage(path + "set",
                "%player%", target.getName(),
                "%nickname%", nickname));
        return true;
    }
}
