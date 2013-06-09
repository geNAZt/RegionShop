package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.Model.ShopItemEnchantments;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class ItemConverter {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;
    }

    public static ItemStack fromDBItem(ShopItems item) {
        ItemStack iStack = new ItemStack(Material.getMaterial(item.getItemID()), 1);
        iStack.getData().setData(item.getDataID());
        iStack.setDurability(item.getDurability());

        List<ShopItemEnchantments> enchants = plugin.getDatabase().find(ShopItemEnchantments.class).
                where().
                    eq("shop_item_id", item.getId()).
                findList();

        if(enchants.size() > 0) {
            for(ShopItemEnchantments ench : enchants) {
                Enchantment enchObj = new EnchantmentWrapper(ench.getEnchId()).getEnchantment();
                iStack.addEnchantment(enchObj, ench.getEnchLvl());
            }
        }

        if(item.getCustomName() != null) {
            ItemMeta iMeta = iStack.getItemMeta();
            iMeta.setDisplayName(item.getCustomName());
            iStack.setItemMeta(iMeta);
        }

        return iStack;
    }

    public static ShopItems toDBItem(ItemStack item, World world, String owner, String region, Integer buy, Integer sell, Integer amount) {
        ShopItems newItem = new ShopItems();
        newItem.setWorld(world.getName());
        newItem.setCurrentAmount(item.getAmount());
        newItem.setItemID(item.getType().getId());
        newItem.setDurability(item.getDurability());
        newItem.setOwner(owner);
        newItem.setRegion(region);
        newItem.setDataID(item.getData().getData());
        newItem.setStackable(item.getMaxStackSize() != 1);
        newItem.setCustomName((item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null);

        newItem.setBuy(buy);
        newItem.setSell(sell);
        newItem.setUnitAmount(amount);

        plugin.getDatabase().save(newItem);

        Map<Enchantment, Integer> itemEnch = item.getEnchantments();
        if(itemEnch != null) {
            for(Map.Entry<Enchantment, Integer> entry : itemEnch.entrySet()) {
                ShopItemEnchantments ench = new ShopItemEnchantments();
                ench.setEnchId(entry.getKey().getId());
                ench.setEnchLvl(entry.getValue());
                ench.setShopItemId(newItem.getId());

                plugin.getDatabase().save(ench);
            }
        }

        return newItem;
    }
}
