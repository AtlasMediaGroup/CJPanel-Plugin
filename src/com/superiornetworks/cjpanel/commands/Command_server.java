package com.superiornetworks.cjpanel.commands;

import com.superiornetworks.cjpanel.CJPanel;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import com.superiornetworks.cjpanel.CJP_PanelAccess;

@CommandPermissions(source = SourceType.PLAYER)
public class Command_server extends BukkitCommand
{

    @Override
    public boolean run(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        PanelMode mode = PanelMode.DONOTHING;

        if (!TFM_AdminList.isSuperAdmin(commandSender))
        {
            commandSender.sendMessage(CJPanel.MSG_NO_PERMS);
        }
        else
        {

            if (args.length == 0)
            {
                return false;
            }

            if (args.length == 1)
            {
                if (args[0].equals("reboot"))
                {
                    if (TFM_AdminList.isSeniorAdmin(commandSender, true))
                    {
                        mode = (PanelMode.REBOOT);
                    }
                    else
                    {
                        commandSender.sendMessage(CJPanel.MSG_NO_PERMS);
                    }
                }

                if (args[0].equals("kill"))
                {
                    if (TFM_AdminList.isSeniorAdmin(commandSender, true))
                    {
                        mode = (PanelMode.KILL);
                    }
                    else
                    {
                        commandSender.sendMessage(CJPanel.MSG_NO_PERMS);
                    }
                }

                if (args[0].equals("wipeflatlands"))
                {
                    if (TFM_AdminList.isTelnetAdmin(commandSender, true))
                    {
                        mode = (PanelMode.WIPEFLAT);
                    }
                    else
                    {
                        commandSender.sendMessage(CJPanel.MSG_NO_PERMS);
                    }
                }

                if (args[0].equals("essentialwipe"))
                {
                    if (TFM_AdminList.isSeniorAdmin(commandSender, true))
                    {
                        mode = (PanelMode.ESSWIPE);
                    }
                    else
                    {
                        commandSender.sendMessage(CJPanel.MSG_NO_PERMS);
                    }
                }
            }

            if (args.length == 2)
            {
                return false;

            }

            PanelAccess(commandSender, (Player) commandSender, mode);

            return true;
        }
        return false;
    }

    public static void PanelAccess(final CommandSender sender, final Player target, final Command_server.PanelMode mode)
    {
        CJP_PanelAccess.PanelAccess(sender, target.getName(), target.getAddress().getAddress().getHostAddress().trim(), mode);
    }

    public static enum PanelMode
    {

        REBOOT("restart"), DONOTHING("donothing"), KILL("kill"), WIPEFLAT("wipeflatlands"), ESSWIPE("clearuserdata");
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
}
