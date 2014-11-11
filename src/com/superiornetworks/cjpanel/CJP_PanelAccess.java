package com.superiornetworks.cjpanel;

import com.superiornetworks.cjpanel.commands.Command_server;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import com.superiornetworks.cjpanel.CJP_UrlBuilder;

public class CJP_PanelAccess
{

    public static void PanelAccess(final CommandSender sender, final String targetName, final String targetIP, final Command_server.PanelMode mode)
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

                    URL url = new CJP_UrlBuilder.URLBuilder(PanelURL)
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
                                else
                                {
                                    if (responseCode == 123)
                                    {
                                        sender.sendMessage(ChatColor.GREEN + "Connection to the Panel API Established. Request to " + mode.toString() + " has been recieved.");
                                    }
                                    else
                                    {
                                        if (responseCode == 201)
                                        {
                                            sender.sendMessage(ChatColor.GREEN + "A connection to the panel has been established! An action is now required.");
                                        }
                                        else
                                        {
                                            if (responseCode == 121)
                                            {
                                                sender.sendMessage(ChatColor.RED + "The API has been disabled on the Webserver. Please contact a CJFreedomMod Developer ASAP! ");
                                            }
                                            else
                                            {
                                                if (responseCode == 122)
                                                {
                                                    sender.sendMessage(ChatColor.RED + "The Key located in the servers properties file does not mach the key located on the webserver - Please contact a CJFreedomMod developer ASAP!");
                                                }
                                                else
                                                {
                                                    sender.sendMessage(ChatColor.RED + "There has been a General error conncting to the API - Contact a CJFreedomMod Developer ASAP");
                                                }
                                            }
                                        }
                                    }
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

}
