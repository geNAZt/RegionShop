package net.cubespace.RegionShop.Interface.CLI.Commands;

import com.j256.ormlite.stmt.QueryBuilder;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Chest;
import net.cubespace.RegionShop.Database.Table.ItemMeta;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

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
        try {
            QueryBuilder<ItemMeta, Integer> itemMetaQb = Database.getDAO(ItemMeta.class).queryBuilder();

            itemMetaQb.where().
                    eq("itemID", itemInHand.getType().getId()).
                    and().
                    eq("dataValue", itemInHand.getData().getData());

            java.util.List<Items> items = Database.getDAO(Items.class).queryBuilder().
                    join(itemMetaQb).
                    where().
                    eq("itemstorage_id", region.getItemStorage().getId()).
                    query();

            //Check if list is empty
            if(items.isEmpty()) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_NoBuy);
                return;
            }

            net.cubespace.RegionShop.Core.Sell.sell(itemInHand, items, player, region);
        } catch (SQLException e) {
            Logger.error("Could not get Items", e);
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_NoBuy);
            return;
        }
    }
}
