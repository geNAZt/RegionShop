package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItemEnchantmens;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopDetail {
    private RegionShopPlugin plugin;

    public ShopDetail(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p, Integer itemID) {
        ShopItems item = plugin.getDatabase().
                find(ShopItems.class).
                where().
                eq("id", itemID).
                findUnique();

        if (item != null) {
            ItemStack iStack = new ItemStack(Material.getMaterial(item.getItemID()), 1);
            iStack.getData().setData(item.getDataID());
            iStack.setDurability(item.getDurability());

            String niceItemName = ItemName.nicer(iStack.getType().toString());
            String itemName = ItemName.getDataName(iStack) + niceItemName;

            Integer dmg = 0;

            if (iStack.getDurability() > 0) {
                dmg = (iStack.getType().getMaxDurability() / iStack.getDurability())*100;
            }

            p.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Detail View of Item " + ChatColor.GREEN + itemName + " " + ChatColor.GRAY + "#" + item.getId());
            p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + item.getSell() + "$ " + ChatColor.DARK_GREEN + " - " + ChatColor.RED + dmg + "% "+ ChatColor.DARK_GREEN + "Damaged");
            p.sendMessage(Chat.getPrefix() + " ");

            List<ShopItemEnchantmens> enchants = plugin.getDatabase().find(ShopItemEnchantmens.class).
                    where().
                        eq("shop_item_id", item.getId()).
                    findList();

            if(enchants.size() > 0) {
                p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Enchantments:");

                for(ShopItemEnchantmens ench : enchants) {
                    Enchantment enchObj = new EnchantmentWrapper(ench.getEnchId()).getEnchantment();

                    p.sendMessage(Chat.getPrefix() + "    " + ChatColor.DARK_GREEN + ItemName.nicer(enchObj.getName()) + " Level " + ChatColor.GREEN + ench.getEnchLvl());
                }
            }

            return true;
        }

        p.sendMessage(Chat.getPrefix() + "This Shopitem couldn't be found");
        return false;
    }
}
