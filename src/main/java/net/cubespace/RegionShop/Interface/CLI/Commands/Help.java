package net.cubespace.RegionShop.Interface.CLI.Commands;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Interface.CLI.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class Help implements CLICommand {
    @Command(command="shop help", arguments=0, helpKey="Command_Help_HelpText", helpPage="none", permission="rs.command.help")
    public static void help(CommandSender sender, String[] args) {
        ArrayList<String> helpPage;
        String page = "default";

        if(args.length != 0) {
            helpPage = CommandExecutor.getHelpPage(args[0]);
            page = args[0];
        } else {
            helpPage = CommandExecutor.getHelpPage();
        }

        if(!sender.hasPermission("rs.help." + page)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Help_NoPermission);
            return;
        }

        ArrayList<String> helpHeader = ConfigManager.language.Command_Help_Header;

        for(String headerLine : helpHeader) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + headerLine.replace("%page", page));
        }

        if(helpPage == null) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Help_NoHelp);
            return;
        }

        for(String helpLine : helpPage) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + helpLine);
        }
    }
}
