package co.ignitus.mysqlnicks;

import co.ignitus.mysqlnicks.commands.ExportCMD;
import co.ignitus.mysqlnicks.commands.ImportCMD;
import co.ignitus.mysqlnicks.commands.NickCMD;
import co.ignitus.mysqlnicks.util.DataUtil;
import co.ignitus.mysqlnicks.util.PlaceholderUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class MySQLNicks extends JavaPlugin {

    @Getter
    private static MySQLNicks instance;

    private final CommandSender cs = Bukkit.getConsoleSender();

    @Override
    public void onEnable() {
        instance = this;
        cs.sendMessage(ChatColor.GREEN + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        cs.sendMessage(ChatColor.GREEN + "  Enabling MySQLNicks");
        cs.sendMessage(ChatColor.GREEN + " Developed by Ignitus Co.");
        cs.sendMessage(ChatColor.GREEN + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        saveDefaultConfig();

        if (!DataUtil.createDatabases()) {
            cs.sendMessage(ChatColor.RED + "[MySQLNicks] Unable to connect to the database. Disabling plugin...");
            getPluginLoader().disablePlugin(this);
            return;
        }
        cs.sendMessage(ChatColor.GREEN + "[MySQLNicks] Successfully established a connection with the database.");
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            cs.sendMessage(ChatColor.RED + "[MySQLNicks] PlaceholderAPI not found. Disabling plugin...");
            getPluginLoader().disablePlugin(this);
            return;
        }
        new PlaceholderUtil(this).register();
        cs.sendMessage(ChatColor.GREEN + "[MySQLNicks] Hooking into PlaceholderAPI");

        if (Bukkit.getPluginManager().isPluginEnabled("Essentials"))
            cs.sendMessage(ChatColor.GREEN + "[MySQLNicks] Hooking into Essentials.");
        if (Bukkit.getPluginManager().isPluginEnabled("CMI"))
            cs.sendMessage(ChatColor.GREEN + "[MySQLNicks] Hooking into CMI.");

        getCommand("nick").setExecutor(new NickCMD());
        getCommand("importnicks").setExecutor(new ImportCMD());
        getCommand("exportnicks").setExecutor(new ExportCMD());
    }

    @Override
    public void onDisable() {
        cs.sendMessage(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        cs.sendMessage(ChatColor.RED + "  Disabling MySQLNicks");
        cs.sendMessage(ChatColor.RED + " Developed by Ignitus Co.");
        cs.sendMessage(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
    }
}
