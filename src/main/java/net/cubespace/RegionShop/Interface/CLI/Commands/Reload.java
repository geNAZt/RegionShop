package net.cubespace.RegionShop.Interface.CLI.Commands;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Tasks.IntegrateServershop;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import org.bukkit.command.CommandSender;

public class Reload implements CLICommand {
    @Command(command = "shop admin reload", arguments = 0, helpKey = "Command_Reload_HelpText", helpPage = "admin", permission = "rs.command.admin.reload")
    public static void reload(CommandSender sender, String[] args) {
        ConfigManager.language.reload();
        ConfigManager.main.reload();
        ConfigManager.servershop.reload();

        Plugin.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Plugin.getInstance(), new IntegrateServershop(), 100);


        sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Reload_Success);
    }
}
