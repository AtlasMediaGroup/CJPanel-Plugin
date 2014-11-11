package com.superiornetworks.cjpanel;

import com.superiornetworks.cjpanel.commands.Command_server;
import net.pravian.bukkitlib.command.BukkitCommandHandler;
import net.pravian.bukkitlib.config.YamlConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import me.StevenLawson.TotalFreedomMod.TFM_Log;

public class CJPanel extends JavaPlugin
{

    public static BukkitCommandHandler handler;

    public static YamlConfig config;

    public static CJPanel plugin;

    public static final String MSG_NO_PERMS = ChatColor.RED + "You do not have permission to use this command.";

    @Override
    public void onEnable()
    {
        plugin = this;

        handler = new BukkitCommandHandler(plugin);
        handler.setCommandLocation(Command_server.class.getPackage());
        handler.setPermissionMessage(MSG_NO_PERMS);

        config = new YamlConfig(plugin, "config.yml", true);
        config.load();
    }

    @Override
    public void onDisable()
    {
        TFM_Log.info("The CJPanel API Link has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return handler.handleCommand(sender, cmd, commandLabel, args);
    }

}
