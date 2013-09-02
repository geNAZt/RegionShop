package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Database.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Database.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Transaction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopRemove extends ShopCommand {
    private static RegionShopPlugin plugin;

    public ShopRemove(RegionShopPlugin pl) {
        plugin = pl;
    }

    @Override
    public int getHelpPage() {
        return 2;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop remove " + ChatColor.RED + "shopItemID" + ChatColor.RESET + ": Remove the "+ ChatColor.RED + "shopItemID" + ChatColor.RESET + " out of the Shop"};
    }

    @Override
    public String getCommand() {
        return "remove";
    }

    @Override
    public String getPermissionNode() {
        return "rs.stock.remove";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        Integer shopItemId = 0;

        try {
            shopItemId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "Only numbers as shopItemID value allowed");
            return;
        }

        ShopItems item = plugin.getDatabase().find(ShopItems.class).
                    where().
                        eq("id", shopItemId).
                    findUnique();

        if(item != null) {
            if(!item.getShopRegion().getOwner().equals(player.getName())) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This is not your Item");
                return;
            }

            ItemStack iStack = ItemConverter.fromDBItem(item);
            iStack.setAmount(item.getCurrentAmount());
            HashMap<Integer, ItemStack> notFitItems = player.getInventory().addItem(iStack);
            if (!notFitItems.isEmpty()) {
                for(Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                    item.setCurrentAmount(item.getCurrentAmount() - notFitItem.getValue().getAmount());
                }

                plugin.getDatabase().update(item);

                Transaction.generateTransaction(player, ShopTransaction.TransactionType.REMOVE, item.getRegion(), item.getWorld(), item.getOwner(), iStack.getTypeId(), iStack.getAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());

                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Not all Items has fit into your Inventory. Please remove again if you have more Place");
                return;
            }

            plugin.getDatabase().delete(item);

            Transaction.generateTransaction(player, ShopTransaction.TransactionType.REMOVE, item.getRegion(), item.getWorld(), item.getOwner(), iStack.getTypeId(), iStack.getAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());
            player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Item has been removed from your Shop");
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "No Item found for this shopItemID");
        }
    }
}
