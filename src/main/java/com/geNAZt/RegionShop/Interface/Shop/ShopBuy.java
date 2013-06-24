package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Model.ShopTransaction;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Transaction.Transaction;
import com.geNAZt.RegionShop.Util.*;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopBuy extends ShopCommand {
    private final Plugin plugin;

    public ShopBuy(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop buy " + ChatColor.RED + "shopItemID " +  ChatColor.GREEN + "amount" + ChatColor.RESET + ": Buy (" + ChatColor.GREEN + "amount" + ChatColor.RESET + " pcs. of) " + ChatColor.RED + "shopItemID " + ChatColor.RESET + "from the shop"};
    }

    @Override
    public String getCommand() {
        return "buy";
    }

    @Override
    public String getPermissionNode() {
        return "rs.buy";
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

        Integer shopItemId, wishAmount;

        try {
            shopItemId = Integer.parseInt(args[0]);
            wishAmount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "Only numbers as shopItemId and amount values allowed");
            return;
        }

        if (PlayerStorage.getPlayer(player) != null) {
            String region = PlayerStorage.getPlayer(player);

            ShopItems item = plugin.getDatabase().find(ShopItems.class).
                        where().
                            conjunction().
                                eq("world", player.getWorld().getName()).
                                eq("region", region).
                                eq("id", shopItemId).
                            endJunction().
                        findUnique();

            if (item == null) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This shopItem could not be found");
                return;
            }

            if (item.getSell() > 0) {
                if (wishAmount > item.getCurrentAmount() && wishAmount != -1) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This shop has not enough items in stock");
                    return;
                }

                if (wishAmount < 1) {
                    wishAmount = 1;
                }

                ItemStack iStack = ItemConverter.fromDBItem(item);
                iStack.setAmount(wishAmount);

                Economy eco = VaultBridge.economy;

                if (eco.has(player.getName(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell())) {
                    HashMap<Integer, ItemStack> notFitItems = player.getInventory().addItem(iStack);
                    if (!notFitItems.isEmpty()) {
                        for(Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                            wishAmount -= notFitItem.getValue().getAmount();
                        }
                    }

                    ProtectedRegion regionObj = WorldGuardBridge.getRegionByString(region, player.getWorld());
                    String shopName = WorldGuardBridge.convertRegionToShopName(regionObj, player.getWorld());
                    if(shopName == null) {
                        shopName = regionObj.getId();
                    }

                    OfflinePlayer owner = plugin.getServer().getOfflinePlayer(item.getOwner());
                    Player onOwner = null;

                    if(owner.isOnline()) {
                        onOwner = (Player) owner;
                    }

                    if (onOwner != null) {
                        onOwner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + player.getDisplayName() + ChatColor.DARK_GREEN + " bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " from your shop (" + ChatColor.GREEN + shopName + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell()) + "$");
                    }

                    eco.withdrawPlayer(player.getName(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell());
                    eco.depositPlayer(item.getOwner(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell());
                    player.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + " for " + ChatColor.GREEN + (((float) wishAmount / (float) item.getUnitAmount()) * (float) item.getSell()) + "$" + ChatColor.DARK_GREEN + " from shop");

                    item.setCurrentAmount(item.getCurrentAmount() - wishAmount);

                    if (item.getCurrentAmount() > 0) {
                        plugin.getDatabase().update(item);
                    } else {
                        if (onOwner != null) {
                            onOwner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "ShopItem " + ChatColor.GREEN + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " is empty. It has been removed from your shop (" + ChatColor.GREEN + shopName + ChatColor.DARK_GREEN + ")");
                        } else {
                            EssentialBridge.sendMail(Chat.getPrefix(), owner, ChatColor.DARK_GREEN + "ShopItem " + ChatColor.GREEN + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " is empty. It has been removed from your shop (" + ChatColor.GREEN + shopName + ChatColor.DARK_GREEN + ")");
                        }

                        plugin.getDatabase().delete(item);
                    }

                    Transaction.generateTransaction(player, ShopTransaction.TransactionType.BUY, region, owner.getName(), item.getItemID(), wishAmount, item.getSell(), 0);
                    Transaction.generateTransaction(owner, ShopTransaction.TransactionType.SELL, region, player.getName(), item.getItemID(), wishAmount, item.getSell(), 0);
                } else {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have not enough money for this. You need "+ (((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell()) + "$");
                    return;
                }
            } else {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop doesn't sell this Item");
                return;
            }

            return;
        }

        //Nothing of all
        player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You are not inside a shop");
    }
}
