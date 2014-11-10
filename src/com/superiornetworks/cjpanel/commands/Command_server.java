package com.superiornetworks.cjpanel.commands;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.superiornetworks.cjpanel.CJPanel;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(source = SourceType.PLAYER)
public class Command_server extends BukkitCommand
{
    @Override
    public boolean run(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        PanelMode mode = PanelMode.DONOTHING;

        if (args.length == 0)
        {
            return false;
        }

        if (args.length == 1)
        {
            if (args[0].equals("reboot"))
            {
                mode = (PanelMode.REBOOT);
            }

            if (args[0].equals("kill"))
            {
                mode = (PanelMode.KILL);
            }

            if (args[0].equals("wipeflatlands"))
            {
                mode = (PanelMode.WIPEFLAT);
            }

            if (args[0].equals("essentialwipe"))
            {
                mode = (PanelMode.ESSWIPE);
            }

            if (args[0].equals("test"))
            {
                mode = (PanelMode.TEST);
            }

        }

        if (args.length == 2)
        {
            return false;

        }

     PanelAccess(commandSender, (Player) commandSender, mode);

        return true;
    }

    public static void PanelAccess(final CommandSender sender, final Player target, final Command_server.PanelMode mode)
    {
        PanelAccess(sender, target.getName(), target.getAddress().getAddress().getHostAddress().trim(), mode);
    }

    public static void PanelAccess(final CommandSender sender, final String targetName, final String targetIP, final PanelMode mode)
    {
        final String PanelURL = CJPanel.config.getString("PANEL_URL");
        final String PanelAPI = CJPanel.config.getString("PANEL_API_KEY");

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
                        }.runTask(TotalFreedomMod.plugin);
                    }
                }
                catch (IOException | IllegalArgumentException | IllegalStateException ex)
                {
                    TFM_Log.severe(ex);
                }
            }
        }.runTaskAsynchronously(TotalFreedomMod.plugin);
    }

    public static enum PanelMode
    {
        REBOOT("restart"), DONOTHING("donothing"), KILL("kill"), WIPEFLAT("wipeflatlands"), ESSWIPE("clearuserdata"), TEST("tester");
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
        private final Map<String, String> queryStringMap = new HashMap<>();

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
            List<String> pairs = new ArrayList<>();
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