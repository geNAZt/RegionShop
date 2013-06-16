package com.geNAZt.RegionShop.Interface.Shop;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopSell extends ShopCommand {
    private final Plugin plugin;

    public ShopSell(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop sell "+ ChatColor.RESET + ": Sell the current Item in Hand to the shop"};
    }

    @Override
    public String getCommand() {
        return "sell";
    }

    @Override
    public String getPermissionNode() {
        return "rs.sell";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (PlayerStorage.getPlayer(player) != null) {
            String region = PlayerStorage.getPlayer(player);

            ItemStack itemInHand = player.getItemInHand();

            if(itemInHand == null || itemInHand.getType().getId() == 0) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have no item in the hand");
                return;
            }

            if(!itemInHand.getEnchantments().isEmpty() || itemInHand.getItemMeta().hasDisplayName()) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You can't sell enchanted / custom renamed Items into a shop");
            }

            List<ShopItems> items = plugin.getDatabase().find(ShopItems.class).
                    where().
                        conjunction().
                            eq("world", player.getWorld().getName()).
                            eq("region", region).
                            eq("item_id", itemInHand.getType().getId()).
                            eq("data_id", itemInHand.getData().getData()).
                            eq("durability", itemInHand.getDurability()).
                            eq("custom_name", (itemInHand.getItemMeta().hasDisplayName()) ? itemInHand.getItemMeta().getDisplayName() : null).
                        endJunction().
                    findList();

            if(items.isEmpty()) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This shop does not buy this item");
                return;
            }

            for(ShopItems item : items) {
                if (item != null && item.getBuy() > 0) {
                    Economy eco = VaultBridge.economy;

                    if (eco.has(item.getOwner(), itemInHand.getAmount() * item.getBuy())) {
                        ProtectedRegion regionObj = WorldGuardBridge.getRegionByString(region, player.getWorld());
                        String shopName = WorldGuardBridge.convertRegionToShopName(regionObj, player.getWorld());
                        if(shopName == null) {
                            shopName = regionObj.getId();
                        }

                        Player owner = plugin.getServer().getPlayer(item.getOwner());
                        if (owner != null) {
                            owner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + player.getDisplayName() + ChatColor.DARK_GREEN + " has sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + ChatColor.DARK_GREEN + " to your shop (" + ChatColor.GREEN + shopName + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getBuy()) + "$");
                        }

                        eco.withdrawPlayer(item.getOwner(), itemInHand.getAmount() * item.getBuy());
                        eco.depositPlayer(player.getName(), itemInHand.getAmount() * item.getBuy());
                        player.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + " for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getBuy()) + "$" + ChatColor.DARK_GREEN + " to shop");

                        player.getInventory().remove(itemInHand);
                        item.setCurrentAmount(item.getCurrentAmount() + itemInHand.getAmount());
                        plugin.getDatabase().update(item);

                        Transaction.generateTransaction(player, ShopTransaction.TransactionType.SELL, region, owner.getName(), item.getItemID(), itemInHand.getAmount(), 0, item.getBuy());
                        Transaction.generateTransaction(owner, ShopTransaction.TransactionType.BUY, region, player.getName(), item.getItemID(), itemInHand.getAmount(), 0, item.getBuy());

                        return;
                    }
                }
            }

            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "None of the Item Owners has enough Money");
            return;
        }

        //Nothing of all
        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a shop");
    }
}
