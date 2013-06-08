package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.*;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopSell {
    private RegionShopPlugin plugin;

    public ShopSell(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p) {
        if (PlayerStorage.getPlayer(p) != null) {
            String region = PlayerStorage.getPlayer(p);

            ItemStack itemInHand = p.getItemInHand();

            if(!itemInHand.getEnchantments().isEmpty() || itemInHand.getItemMeta().hasDisplayName()) {
                p.sendMessage(Chat.getPrefix() + "You can't sell enchanted / custom renamed Items into a shop");
            }

            ShopItems item = plugin.getDatabase().find(ShopItems.class).
                    where().
                        conjunction().
                            eq("world", p.getWorld().getName()).
                            eq("region", region).
                            eq("item_id", itemInHand.getType().getId()).
                            eq("data_id", itemInHand.getData().getData()).
                            eq("durability", itemInHand.getDurability()).
                            eq("custom_name", (itemInHand.getItemMeta().hasDisplayName()) ? itemInHand.getItemMeta().getDisplayName() : null).
                        endJunction().
                    findUnique();

            if (item.getBuy() > 0) {
                Economy eco = VaultBridge.economy;

                if (eco.has(item.getOwner(), itemInHand.getAmount() * item.getBuy())) {
                    Player owner = plugin.getServer().getPlayer(item.getOwner());
                    if (owner != null) {
                        owner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + p.getDisplayName() + ChatColor.DARK_GREEN + " has sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + ChatColor.DARK_GREEN + " to your Shop (" + ChatColor.GREEN + region + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getBuy()) + "$");
                    }

                    eco.withdrawPlayer(item.getOwner(), itemInHand.getAmount() * item.getBuy());
                    eco.depositPlayer(p.getName(), itemInHand.getAmount() * item.getBuy());
                    plugin.getServer().getPlayer(item.getOwner()).sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + " for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getBuy()) + "$" + ChatColor.DARK_GREEN + " to Shop");

                    p.getInventory().remove(itemInHand);
                    item.setCurrentAmount(item.getCurrentAmount() + itemInHand.getAmount());
                    plugin.getDatabase().update(item);
                } else {
                    p.sendMessage(Chat.getPrefix() + "This ShopItem Owner hasn't enough money");
                }
            } else {
                p.sendMessage(Chat.getPrefix() + "This Shop doesn't buy this Item");
            }

            return true;
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + "You aren't inside a Shop");
        return false;
    }
}
