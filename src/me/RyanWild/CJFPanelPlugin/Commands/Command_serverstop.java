package me.RyanWild.CJFPanelPlugin.Commands;

import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.RyanWild.CJFPanelPlugin.CJFreedomPanelLink;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(source = SourceType.PLAYER, usage = "Usage: /<command>", permission = "cjpanel.stop")
public class Command_serverstop extends BukkitCommand
{

    @Override
    public boolean run(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        PanelMode mode = PanelMode.DONOTHING;

        if (args.length == 0)
        {
            Bukkit.broadcastMessage(ChatColor.RED + "Server Kill Activated");
            mode = (PanelMode.STOP);
        }

        if (args.length != 0)
        {
            return showUsage();
        }

        PanelAccess(commandSender, commandSenderPlayer, mode);

        return true;
    }

    public static void PanelAccess(final CommandSender sender, final Player target, final Command_serverstop.PanelMode mode)
    {
        PanelAccess(sender, target.getName(), target.getAddress().getAddress().getHostAddress().trim(), mode);
    }

    public static void PanelAccess(final CommandSender sender, final String targetName, final String targetIP, final PanelMode mode)
    {
        final String PanelURL = CJFreedomPanelLink.config.getString("PANEL_URL");
        final String PanelAPI = CJFreedomPanelLink.config.getString("PANEL_API_KEY");

        if (PanelURL == null || PanelAPI == null || PanelURL.isEmpty() || PanelAPI.isEmpty())
        {
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (sender != null)
                    {
                        sender.sendMessage(ChatColor.YELLOW + "Connecting you to the panel API - Please Standby...");
                    }

                    URL url = new URLBuilder(PanelURL)
                            .addQueryParameter("apikey", PanelAPI)
                            .addQueryParameter("action", mode.toString())
                            .addQueryParameter("name", targetName)
                            .getURL();

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(1000 * 5);
                    connection.setReadTimeout(1000 * 5);
                    connection.setUseCaches(false);

                    final int responseCode = connection.getResponseCode();

                    if (sender != null)
                    {
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if (responseCode == 200)
                                {
                                    sender.sendMessage(ChatColor.RED + "The Panel API Access status is UNKNOWN - Contact a CJFreedomMod developer ASAP!");
                                }
                                else if (responseCode == 123)
                                {
                                    sender.sendMessage(ChatColor.GREEN + "Connection to the Panel API Established. Request to " + mode.toString() + " has been recieved.");
                                }
                                else if (responseCode == 201)
                                {
                                    sender.sendMessage(ChatColor.GREEN + "A connection to the panel has been established! An action is now required.");
                                }
                                else if (responseCode == 121)
                                {
                                    sender.sendMessage(ChatColor.RED + "The API has been disabled on the Webserver. Please contact a CJFreedomMod Developer ASAP! ");
                                }
                                else if (responseCode == 122)
                                {
                                    sender.sendMessage(ChatColor.RED + "The Key located in the servers properties file does not mach the key located on the webserver - Please contact a CJFreedomMod developer ASAP!");
                                }
                                else
                                {
                                    sender.sendMessage(ChatColor.RED + "There has been a General error conncting to the API - Contact a CJFreedomMod Developer ASAP");
                                }
                            }

                        }.runTask(CJFreedomPanelLink.plugin);
                    }
                }
                catch (Exception ex)
                {
                    CJFreedomPanelLink.logger.severe("Error");
                }
            }

        }.runTaskAsynchronously(CJFreedomPanelLink.plugin);
    }

    public static enum PanelMode
    {

        DONOTHING("donothing"), STOP("stop");

        private final String mode;

        private PanelMode(String mode)
        {
            this.mode = mode;
        }

        @Override
        public String toString()
        {
            return mode;
        }

    }

    private static class URLBuilder
    {

        private final String requestPath;

        private final Map<String, String> queryStringMap = new HashMap<String, String>();

        public URLBuilder(String requestPath)
        {
            this.requestPath = requestPath;
        }

        public URLBuilder addQueryParameter(String key, String value)
        {
            queryStringMap.put(key, value);
            return this;
        }

        public URL getURL() throws MalformedURLException
        {
            List<String> pairs = new ArrayList<String>();
            Iterator<Entry<String, String>> it = queryStringMap.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<String, String> pair = it.next();
                pairs.add(pair.getKey() + "=" + pair.getValue());
            }

            return new URL(requestPath + "?" + StringUtils.join(pairs, "&"));
        }

    }
}