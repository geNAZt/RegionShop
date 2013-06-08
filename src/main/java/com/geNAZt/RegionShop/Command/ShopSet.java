package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopSet {
    private RegionShopPlugin plugin;

    public ShopSet(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p, Integer shopItemId, Integer sell, Integer buy, Integer amount) {
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

            ItemStack itemStack = new ItemStack(Material.getMaterial(item.getItemID()), 1);
            itemStack.getData().setData(item.getDataID());
            itemStack.setDurability(item.getDurability());

            String itemName;
            if (itemStack.getItemMeta().hasDisplayName()) {
                itemName = ItemName.getDataName(itemStack) + itemStack.getItemMeta().getDisplayName();
            } else {
                itemName = ItemName.getDataName(itemStack) + itemStack.getType().toString();
            }

            p.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Item " + ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " now sells for " + ChatColor.GREEN + item.getSell() + ChatColor.GOLD + ", buys for " + ChatColor.GREEN + item.getBuy() + ChatColor.GOLD + ", per " + ChatColor.GREEN + item.getUnitAmount() + ChatColor.GOLD + " unit(s)");
        }

        return false;
    }
}
