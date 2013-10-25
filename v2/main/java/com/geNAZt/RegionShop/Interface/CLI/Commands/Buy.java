package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Buy implements CLICommand {
    @Command(command="shop buy", arguments=1, helpKey="Command_Buy_HelpText", helpPage="consumer", permission="rs.command.buy")
    public static void buy(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        //Convert args
        Integer shopItemId, wishAmount = 1;

        try {
            if(args.length > 1) {
                wishAmount = Integer.parseInt(args[1]);
            }

            shopItemId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_InvalidArguments);
            return;
        }

        //Is player in region ?
        if (!InRegion.has(player)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_NotInRegion);
            return;
        }

        Region region = InRegion.get(player);

        //Get the item
        Items item = Database.getServer().find(Items.class).
                where().
                    conjunction().
                        eq("id", shopItemId).
                    endJunction().
                findUnique();

        //No luck for you => no item found
        if (item == null) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_ItemNotFound);
            return;
        }

        com.geNAZt.RegionShop.Core.Buy.buy(item, player, region, wishAmount);
    }
}
