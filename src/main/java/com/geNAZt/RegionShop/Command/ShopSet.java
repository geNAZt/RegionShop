package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopSet {
    private final RegionShopPlugin plugin;

    public ShopSet(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void execute(Player p, Integer shopItemId, Integer sell, Integer buy, Integer amount) {
        ShopItems item = plugin.getDatabase().
                find(ShopItems.class).
                where().
                    eq("id", shopItemId).
                    eq("owner", p.getName()).
                findUnique();

        if (item == null) {
            p.sendMessage(Chat.getPrefix() + ChatColor.RED + "This item could not be found or you aren't the owner of it.");
        } else {
            item.setBuy(buy);
            item.setSell(sell);
            item.setUnitAmount(amount);

            plugin.getDatabase().update(item);

            ItemStack itemStack = ItemConverter.fromDBItem(item);

            String itemName;
            if (itemStack.getItemMeta().hasDisplayName()) {
                itemName = ItemName.getDataName(itemStack) + itemStack.getItemMeta().getDisplayName();
            } else {
                itemName = ItemName.getDataName(itemStack) + itemStack.getType().toString();
            }

            p.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Item " + ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " now sells for " + ChatColor.GREEN + item.getSell() + ChatColor.GOLD + ", buys for " + ChatColor.GREEN + item.getBuy() + ChatColor.GOLD + ", per " + ChatColor.GREEN + item.getUnitAmount() + ChatColor.GOLD + " unit(s)");
        }
    }
}
