package com.geNAZt.RegionShop.Interface.ServerShop;

import com.geNAZt.RegionShop.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopTransaction;
import com.geNAZt.RegionShop.ServerShop.Price;
import com.geNAZt.RegionShop.ServerShop.PriceStorage;
import com.geNAZt.RegionShop.Transaction.Transaction;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 22.06.13
 */
public class Buy extends ShopCommand {
    public Buy(Plugin plugin) {

    }

    @Override
    public int getHelpPage() {
        return 0;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop server buy " + ChatColor.RED + "itemID " +  ChatColor.GREEN + "amount" + ChatColor.RESET + ": Buy an itemID out of the Server Shop"};
    }

    @Override
    public String getCommand() {
        return "buy";
    }

    @Override
    public String getPermissionNode() {
        return "rs.server.buy";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        //Convert args
        String amountStr = "1";
        if(args.length > 1) {
            amountStr = args[1];
        }

        Integer itemId, wishAmount;
        Byte dataValue;
        String itemIDStr, dataValueStr;

        if(args[0].contains(":")) {
            String[] split = args[0].split(":");
            itemIDStr = split[0];
            dataValueStr = split[1];
        } else {
            itemIDStr = args[0];
            dataValueStr = "0";
        }

        try {
            itemId = Integer.parseInt(itemIDStr);
            wishAmount = Integer.parseInt(amountStr);
            dataValue = Byte.parseByte(dataValueStr);
        } catch (NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "Only numbers as itemId and amount values allowed");
            return;
        }

        if (wishAmount < 1) {
            wishAmount = 1;
        }

        ItemStack iStack = new ItemStack(itemId, 1);

        if(dataValue != 0) {
            iStack.getData().setData(dataValue);
        }

        Price price = PriceStorage.get(iStack);

        if(price == null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "ItemID could not be found inside the Servershop");
            return;
        }

        Economy eco = VaultBridge.economy;
        Double sellPrice = wishAmount * price.getCurrentSell();
        if(eco.has(player.getName(), sellPrice)) {
            HashMap<Integer, ItemStack> notFitItems = player.getInventory().addItem(iStack);
            if (!notFitItems.isEmpty()) {
                for(Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                    wishAmount -= notFitItem.getValue().getAmount();
                }
            }

            sellPrice = wishAmount * price.getCurrentSell();

            eco.withdrawPlayer(player.getName(), sellPrice);
            player.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + " for " + ChatColor.GREEN + sellPrice + "$" + ChatColor.DARK_GREEN + " from Servershop");
            Transaction.generateTransaction(player, ShopTransaction.TransactionType.BUY, "SERVERSHOP", "server", iStack.getTypeId(), wishAmount, price.getCurrentSell(), 0);
            price.setSold(price.getSold() + wishAmount);
            PriceStorage.add(iStack, price);
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have not enough money for this. You need "+ sellPrice + "$");
            return;
        }
    }
}
