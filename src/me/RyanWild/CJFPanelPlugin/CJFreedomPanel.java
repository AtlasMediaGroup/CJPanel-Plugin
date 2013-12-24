package me.RyanWild.CJFPanelPlugin;

import java.util.logging.Logger;
import me.RyanWild.CJFPanelPlugin.Commands.*;
import net.pravian.bukkitlib.command.BukkitCommandHandler;
import net.pravian.bukkitlib.config.YamlConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CJFreedomPanel extends JavaPlugin
{

    public static final Logger logger = Bukkit.getLogger();

    public static BukkitCommandHandler handler;

    public static YamlConfig config;

    public static CJFreedomPanel plugin;

    public static final String MSG_NO_PERMS = ChatColor.RED + "You do not have permission to use this command.";

    @Override
    public void onEnable()
    {
        plugin = this;

        handler = new BukkitCommandHandler(plugin);
        handler.setCommandLocation(Command_serverkill.class.getPackage());
        handler.setPermissionMessage(MSG_NO_PERMS);

        config = new YamlConfig(plugin, "config.yml", true);
        config.load();
    }

    @Override
    public void onDisable()
    {
        CJFreedomPanel.logger.info("LinkMe Plugin Disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        // This will handle ALL commands from now on DONT TOUCH!!!
        return handler.handleCommand(sender, cmd, commandLabel, args);

    }

}
