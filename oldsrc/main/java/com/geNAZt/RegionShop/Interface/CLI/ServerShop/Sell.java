package com.geNAZt.RegionShop.Interface.CLI.ServerShop;

import com.geNAZt.RegionShop.Bukkit.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.Price;
import com.geNAZt.RegionShop.Database.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Util.Transaction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 22.06.13
 */
public class Sell extends ShopCommand {
    @Override
    public int getHelpPage() {
        return 0;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop server sell " + ChatColor.RESET + ": Sells the current in hand hold Item"};
    }

    @Override
    public String getCommand() {
        return "sell";
    }

    @Override
    public String getPermissionNode() {
        return "rs.server.sell";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack itemInHand = player.getItemInHand();

        if(itemInHand == null || itemInHand.getType().getId() == 0) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have no item in the hand");
            return;
        }

        if(!itemInHand.getEnchantments().isEmpty() || itemInHand.getItemMeta().hasDisplayName()) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You can't sell enchanted / custom renamed Items into a shop");
            return;
        }

        ItemStack priceItemStack = itemInHand.clone();
        priceItemStack.setAmount(1);


        Price price = PriceStorage.get("GLOBAL", priceItemStack);

        if(price == null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "ItemID could not be found inside the Servershop");
            return;
        }

        if(price.getCurrentBuy() > 0.0) {
            Economy eco = VaultBridge.economy;
            Double buyPrice = itemInHand.getAmount() * price.getCurrentBuy();

            eco.depositPlayer(player.getName(), buyPrice);
            player.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + buyPrice + "$" + ChatColor.DARK_GREEN + " to Servershop");
            Transaction.generateTransaction(player, ShopTransaction.TransactionType.SELL, "Servershop", player.getWorld().getName(), "server", itemInHand.getTypeId(), itemInHand.getAmount(), 0.0, price.getCurrentBuy(), 1);
            player.getInventory().removeItem(itemInHand);

            price.setBought(price.getBought() + itemInHand.getAmount());
            PriceStorage.add("GLOBAL", priceItemStack, price);
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "The Servershop does not buy this Item");
        }
    }
}