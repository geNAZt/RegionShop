package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Transaction;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.VaultBridge;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.OfflinePlayer;
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
            sender.sendMessage("Only for Players");
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

        //Check all items
        for(Items item : items) {
            if (item != null && item.getBuy() > 0) {
                Economy eco = VaultBridge.economy;
                Float price = itemInHand.getAmount() * item.getBuy();

                if (eco.has(item.getOwner(), itemInHand.getAmount() * item.getBuy()) || region.getItemStorage().isServershop()) {
                    String itemName = ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString());

                    if(!region.getItemStorage().isServershop()) {
                        OfflinePlayer owner = RegionShopPlugin.getInstance().getServer().getOfflinePlayer(item.getOwner());

                        if (owner != null) {
                            if(owner.isOnline()) {
                                RegionShopPlugin.getInstance().getServer().getPlayer(item.getOwner()).sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_OwnerHint.
                                        replace("%player", player.getDisplayName()).
                                        replace("%amount", ((Integer)itemInHand.getAmount()).toString()).
                                        replace("%item", itemName).
                                        replace("%shop", region.getName()).
                                        replace("%price", price.toString()));
                            }

                            Transaction.generateTransaction(owner, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.BUY, region.getName(), player.getWorld().getName(), player.getName(), item.getMeta().getId().getItemID(), itemInHand.getAmount(), item.getBuy().doubleValue(), 0.0, item.getUnitAmount());
                        }

                        eco.withdrawPlayer(item.getOwner(), itemInHand.getAmount() * item.getBuy());
                    }

                    eco.depositPlayer(player.getName(), itemInHand.getAmount() * item.getBuy());
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_PlayerHint.
                            replace("%player", player.getDisplayName()).
                            replace("%amount", ((Integer)itemInHand.getAmount()).toString()).
                            replace("%item", itemName).
                            replace("%shop", region.getName()).
                            replace("%price", price.toString()));

                    player.getInventory().removeItem(itemInHand);
                    item.setCurrentAmount(item.getCurrentAmount() + itemInHand.getAmount());
                    item.setBought(item.getBought() + itemInHand.getAmount());
                    Database.getServer().update(item);

                    Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.SELL, region.getName(), player.getWorld().getName(), item.getOwner(), item.getMeta().getId().getItemID(), itemInHand.getAmount(), 0.0, item.getBuy().doubleValue(), item.getUnitAmount());

                    return;
                }
            }
        }

        //No item found :(
        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Sell_NotEnoughMoney);
    }
}
