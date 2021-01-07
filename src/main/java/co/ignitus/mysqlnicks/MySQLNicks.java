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
        //Initialises the MySQLNicks instance
        instance = this;
        //Sends plugin information to console
        cs.sendMessage(ChatColor.GREEN + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        cs.sendMessage(ChatColor.GREEN + "  Enabling MySQLNicks");
        cs.sendMessage(ChatColor.GREEN + " Developed by Ignitus Co.");
        cs.sendMessage(ChatColor.GREEN + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        //Creates config.yml if it doesn't exist
        saveDefaultConfig();

        //Attempts to connect to the MySQL database.
        if (!DataUtil.createDatabases()) {
            //If an error occurred, disables the plugin
            cs.sendMessage(ChatColor.RED + "[MySQLNicks] Unable to connect to the database. Disabling plugin...");
            getPluginLoader().disablePlugin(this);
            return;
        }
        //Info message
        cs.sendMessage(ChatColor.GREEN + "[MySQLNicks] Successfully established a connection with the database.");
        //Attempts to hook into PlaceholderAPI
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            //If PlaceholderAPI is not present on the server, disables the plugin.
            cs.sendMessage(ChatColor.RED + "[MySQLNicks] PlaceholderAPI not found. Disabling plugin...");
            getPluginLoader().disablePlugin(this);
            return;
        }
        //Hooks into PlaceholderAPI
        new PlaceholderUtil(this).register();
        cs.sendMessage(ChatColor.GREEN + "[MySQLNicks] Hooking into PlaceholderAPI");

        if (Bukkit.getPluginManager().isPluginEnabled("Essentials"))
            //Hooks into Essentials if present
            cs.sendMessage(ChatColor.GREEN + "[MySQLNicks] Hooking into Essentials.");
        if (Bukkit.getPluginManager().isPluginEnabled("CMI"))
            //Hooks into CMI if present
            cs.sendMessage(ChatColor.GREEN + "[MySQLNicks] Hooking into CMI.");

        //Registers commands
        getCommand("nick").setExecutor(new NickCMD());
        getCommand("importnicks").setExecutor(new ImportCMD());
        getCommand("exportnicks").setExecutor(new ExportCMD());
    }

    @Override
    public void onDisable() {
        //Info message that's sent on plugin disable
        cs.sendMessage(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        cs.sendMessage(ChatColor.RED + "  Disabling MySQLNicks");
        cs.sendMessage(ChatColor.RED + " Developed by Ignitus Co.");
        cs.sendMessage(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
    }
}
