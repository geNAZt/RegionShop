package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.*;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopSell {
    private final RegionShopPlugin plugin;

    public ShopSell(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void execute(Player p) {
        if (PlayerStorage.getPlayer(p) != null) {
            String region = PlayerStorage.getPlayer(p);

            ItemStack itemInHand = p.getItemInHand();

            if(!itemInHand.getEnchantments().isEmpty() || itemInHand.getItemMeta().hasDisplayName()) {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You can't sell enchanted / custom renamed Items into a shop");
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
                        owner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + p.getDisplayName() + ChatColor.DARK_GREEN + " has sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + ChatColor.DARK_GREEN + " to your shop (" + ChatColor.GREEN + region + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getBuy()) + "$");
                    }

                    eco.withdrawPlayer(item.getOwner(), itemInHand.getAmount() * item.getBuy());
                    eco.depositPlayer(p.getName(), itemInHand.getAmount() * item.getBuy());
                    plugin.getServer().getPlayer(item.getOwner()).sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + " for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getBuy()) + "$" + ChatColor.DARK_GREEN + " to shop");

                    p.getInventory().remove(itemInHand);
                    item.setCurrentAmount(item.getCurrentAmount() + itemInHand.getAmount());
                    plugin.getDatabase().update(item);
                } else {
                    p.sendMessage(Chat.getPrefix() + ChatColor.RED + "The owner of this shop has not enough money");
                }
            } else {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "This shop does not buy this item");
            }

            return;
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a shop");
    }
}
