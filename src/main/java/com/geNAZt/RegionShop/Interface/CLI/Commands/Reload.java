package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Config.InvalidConfigurationException;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import org.bukkit.command.CommandSender;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Reload implements CLICommand {
    @Command(command="shop admin reload", arguments=0, helpKey="Command_Reload_HelpText", helpPage="admin", permission="rs.command.admin.reload")
    public static void reload(CommandSender sender, String[] args) {
        try {
            ConfigManager.language.reload();
            ConfigManager.main.reload();
            ConfigManager.expert.reload();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            sender.sendMessage("Fatal error in reloading RegionShop");
        }

        sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Reload_Success);
    }
}
