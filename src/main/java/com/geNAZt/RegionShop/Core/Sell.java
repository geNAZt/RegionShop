package com.geNAZt.RegionShop.Core;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Config.Sub.Group;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Transaction;
import com.geNAZt.RegionShop.Database.Table.ItemStorage;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.VaultBridge;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 18.09.13
 */
public class Sell {
    public static void sell(ItemStack itemStack, List<Items> items, Player player, Region region) {
        java.util.List<com.geNAZt.RegionShop.Database.Table.Player> playerList = region.getOwners();
        boolean isOwner = false;

        for(com.geNAZt.RegionShop.Database.Table.Player player1 : playerList) {
            if(player1.getName().equals(player.getName().toLowerCase())) {
                isOwner = true;
            }
        }

        if(isOwner) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_NotYourItem);
            return;
        }

        //Check if there is Place inside the Shop
        Group group = ConfigManager.main.getGroup(region.getItemStorage().getSetting());
        if(region.getItemStorage().getItemAmount() + itemStack.getAmount() >= group.Storage) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_FullStorage);

            return;
        }

        //Check all items
        for(Items item : items) {
            if (item != null && item.getBuy() > 0) {
                Economy eco = VaultBridge.economy;
                Float price = itemStack.getAmount() * item.getBuy();

                if (eco.has(item.getOwner(), itemStack.getAmount() * item.getBuy()) || region.getItemStorage().isServershop()) {
                    String itemName = ItemName.getDataName(itemStack) + ItemName.nicer(itemStack.getType().toString());

                    if(!region.getItemStorage().isServershop()) {
                        OfflinePlayer owner = RegionShopPlugin.getInstance().getServer().getOfflinePlayer(item.getOwner());

                        if (owner != null) {
                            if(owner.isOnline()) {
                                RegionShopPlugin.getInstance().getServer().getPlayer(item.getOwner()).sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_OwnerHint.
                                        replace("%player", player.getDisplayName()).
                                        replace("%amount", ((Integer)itemStack.getAmount()).toString()).
                                        replace("%item", itemName).
                                        replace("%shop", region.getName()).
                                        replace("%price", price.toString()));
                            }

                            Transaction.generateTransaction(owner, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.BUY, region.getName(), player.getWorld().getName(), player.getName(), item.getMeta().getId().getItemID(), itemStack.getAmount(), item.getBuy().doubleValue(), 0.0, item.getUnitAmount());
                        }

                        eco.withdrawPlayer(item.getOwner(), itemStack.getAmount() * item.getBuy());
                    }

                    eco.depositPlayer(player.getName(), itemStack.getAmount() * item.getBuy());
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_PlayerHint.
                            replace("%player", player.getDisplayName()).
                            replace("%amount", ((Integer)itemStack.getAmount()).toString()).
                            replace("%item", itemName).
                            replace("%shop", region.getName()).
                            replace("%price", price.toString()));

                    player.getInventory().removeItem(itemStack);
                    item.setCurrentAmount(item.getCurrentAmount() + itemStack.getAmount());
                    item.setBought(item.getBought() + itemStack.getAmount());

                    ItemStorage itemStorage = region.getItemStorage();
                    itemStorage.setItemAmount(itemStorage.getItemAmount() + itemStack.getAmount());

                    Database.getServer().update(itemStorage);
                    Database.getServer().update(item);

                    Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.SELL, region.getName(), player.getWorld().getName(), item.getOwner(), item.getMeta().getId().getItemID(), itemStack.getAmount(), 0.0, item.getBuy().doubleValue(), item.getUnitAmount());

                    return;
                }
            }
        }

        //No item found :(
        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_OwnerHasNotEnoughMoney);
    }

    public static void sell(ItemStack itemStack, Items item, Player player, Region region) {
        java.util.List<com.geNAZt.RegionShop.Database.Table.Player> playerList = region.getOwners();
        boolean isOwner = false;

        for(com.geNAZt.RegionShop.Database.Table.Player player1 : playerList) {
            if(player1.getName().equals(player.getName().toLowerCase())) {
                isOwner = true;
            }
        }

        if(isOwner) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_NotYourItem);
            return;
        }

        if(item.getBuy() <= 0.0) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_DoesNotBuy);
            return;
        }

        Group group = ConfigManager.main.getGroup(region.getItemStorage().getSetting());
        if(region.getItemStorage().getItemAmount() + itemStack.getAmount() >= group.Storage) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_FullStorage);

            return;
        }

        Economy eco = VaultBridge.economy;
        Float price = itemStack.getAmount() * item.getBuy();

        if (eco.has(item.getOwner(), price) || region.getItemStorage().isServershop()) {
            String itemName = ItemName.getDataName(itemStack) + ItemName.nicer(itemStack.getType().toString());

            if(!region.getItemStorage().isServershop()) {
                OfflinePlayer owner = RegionShopPlugin.getInstance().getServer().getOfflinePlayer(item.getOwner());

                if (owner != null) {
                    if(owner.isOnline()) {
                        RegionShopPlugin.getInstance().getServer().getPlayer(item.getOwner()).sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_OwnerHint.
                                replace("%player", player.getDisplayName()).
                                replace("%amount", ((Integer) itemStack.getAmount()).toString()).
                                replace("%item", itemName).
                                replace("%shop", region.getName()).
                                replace("%price", price.toString()));
                    }

                    Transaction.generateTransaction(owner,
                            com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.BUY,
                            region.getName(),
                            player.getWorld().getName(),
                            player.getName(),
                            item.getMeta().getId().getItemID(),
                            itemStack.getAmount(),
                            item.getBuy().doubleValue(),
                            0.0,
                            item.getUnitAmount());
                }

                eco.withdrawPlayer(item.getOwner(), price);
            }

            eco.depositPlayer(player.getName(), price);
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_PlayerHint.
                    replace("%player", player.getDisplayName()).
                    replace("%amount", ((Integer) itemStack.getAmount()).toString()).
                    replace("%item", itemName).
                    replace("%shop", region.getName()).
                    replace("%price", price.toString()));

            player.getInventory().removeItem(itemStack);
            item.setCurrentAmount(item.getCurrentAmount() + itemStack.getAmount());
            item.setBought(item.getBought() + itemStack.getAmount());

            ItemStorage itemStorage = region.getItemStorage();
            itemStorage.setItemAmount(itemStorage.getItemAmount() + itemStack.getAmount());

            Database.getServer().update(itemStorage);
            Database.getServer().update(item);

            Transaction.generateTransaction(player,
                    com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.SELL,
                    region.getName(),
                    player.getWorld().getName(),
                    item.getOwner(),
                    item.getMeta().getId().getItemID(),
                    itemStack.getAmount(),
                    0.0,
                    item.getBuy().doubleValue(),
                    item.getUnitAmount());

        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sell_OwnerHasNotEnoughMoney);
        }
    }
}
