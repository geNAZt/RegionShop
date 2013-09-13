package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 13.09.13
 */
public class Add implements CLICommand {
    @Command(command="shop add", arguments=0, permission="rs.command.add", helpKey="Command_Add_HelpText")
    public static void add(CommandSender sender, String[] args) {
        //This command is not enabled for console
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only for Players");
            return;
        }

        //Cast to player
        Player player = (Player) sender;

        com.geNAZt.RegionShop.Core.Add.add(player.getItemInHand(), player, InRegion.get(player), 0,0,0);
    }
}
