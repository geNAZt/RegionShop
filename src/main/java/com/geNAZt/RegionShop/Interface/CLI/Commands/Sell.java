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
import org.bukkit.inventory.ItemStack;

import java.util.List;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Sell implements CLICommand {
    @Command(command="shop sell", permission="rs.command.sell", helpKey="Command_Sell_HelpPage", helpPage="consumer", arguments=0)
    public static void sell(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        //Check if player is inside a Shop
        if (!InRegion.has(player)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_NotInRegion);
            return;
        }

        Region region = InRegion.get(player);
        ItemStack itemInHand = player.getItemInHand();

        //Check if player has item in his hand
        if(itemInHand == null || itemInHand.getType().getId() == 0) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_NoItemInHand);
            return;
        }

        //Check if item is enchanted or renamed
        if(!itemInHand.getEnchantments().isEmpty() || itemInHand.getItemMeta().hasDisplayName()) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_NoEnchantedOrRenamed);
            return;
        }

        //Get the items out of the Database
        List<Items> items = Database.getServer().find(Items.class).
                where().
                    conjunction().
                        eq("itemStorage", region.getItemStorage()).
                        eq("meta.id.itemID", itemInHand.getType().getId()).
                        eq("meta.id.dataValue", itemInHand.getData().getData()).
                    endJunction().
                findList();

        //Check if list is empty
        if(items.isEmpty()) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_NoBuy);
            return;
        }

        com.geNAZt.RegionShop.Core.Sell.sell(itemInHand, items, player, region);
    }
}
