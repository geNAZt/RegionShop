package net.cubespace.RegionShop.Database.Repository;

import com.j256.ormlite.dao.ForeignCollection;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.ItemStorageHolder;
import net.cubespace.RegionShop.Database.Table.ItemMeta;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.Items;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Map;

public class ItemRepository {
    public static ItemStack fromDBItem(Items item) {
        if(Material.getMaterial(item.getMeta().getItemID()) == null) {
            Plugin.getInstance().getLogger().warning("Found an Item which is not in Bukkit: " + item.getId());
            return new ItemStack(Material.AIR);
        }

        ItemStack iStack = new ItemStack(Material.getMaterial(item.getMeta().getItemID()), 1);

        if(item.getMeta().getDataValue() > 0) {
            iStack.getData().setData(item.getMeta().getDataValue());
        }

        if(item.getDurability() > 0) {
            iStack.setDurability(item.getDurability());
        } else {
            iStack.setDurability((short) item.getMeta().getDataValue());
        }

        ForeignCollection<net.cubespace.RegionShop.Database.Table.Enchantment> enchants = item.getEnchantments();
        if(enchants.size() > 0) {
            for(net.cubespace.RegionShop.Database.Table.Enchantment ench : enchants) {
                Enchantment enchObj = new EnchantmentWrapper(ench.getEnchId()).getEnchantment();
                iStack.addEnchantment(enchObj, ench.getEnchLvl());
            }
        }

        if(item.getCustomName() != null) {
            org.bukkit.inventory.meta.ItemMeta iMeta = iStack.getItemMeta();
            iMeta.setDisplayName(item.getCustomName());
            iStack.setItemMeta(iMeta);
        }

        return iStack;
    }

    public static Items toDBItem(ItemStack item, ItemStorageHolder region, String owner, Float buy, Float sell, Integer amount) {
        if(!ItemMetaRepository.isStored(item)) {
            ItemMetaRepository.insert(item);
        }

        ItemMeta itemMeta = ItemMetaRepository.get(item);

        Items newItem = new Items();
        newItem.setMeta(itemMeta);
        newItem.setItemStorage(region.getItemStorage());
        newItem.setCurrentAmount(item.getAmount());
        newItem.setDurability(item.getDurability());
        newItem.setOwner(owner);
        newItem.setCustomName((item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null);

        newItem.setBuy(buy);
        newItem.setSell(sell);
        newItem.setUnitAmount(amount);

        Map<Enchantment, Integer> itemEnch = item.getEnchantments();
        ForeignCollection<net.cubespace.RegionShop.Database.Table.Enchantment> foreignCollection = newItem.getEnchantments();

        if(itemEnch != null) {
            for(Map.Entry<Enchantment, Integer> entry : itemEnch.entrySet()) {
                net.cubespace.RegionShop.Database.Table.Enchantment ench = new net.cubespace.RegionShop.Database.Table.Enchantment();
                ench.setEnchId(entry.getKey().getId());
                ench.setEnchLvl(entry.getValue());
                ench.setItem(newItem);

                try {
                    Database.getDAO(net.cubespace.RegionShop.Database.Table.Enchantment.class).create(ench);
                    foreignCollection.add(ench);
                } catch (SQLException e) {
                    Plugin.getInstance().getLogger().warning("Could not save enchantment");
                    e.printStackTrace();
                }
            }
        }

        try {
            Database.getDAO(Items.class).create(newItem);
        } catch (SQLException e) {
            Plugin.getInstance().getLogger().severe("Could not store new Item");
            e.printStackTrace();
            return null;
        }

        region.getItemStorage().setItemAmount(region.getItemStorage().getItemAmount() + item.getAmount());

        try {
            Database.getDAO(ItemStorage.class).update(region.getItemStorage());
        } catch (SQLException e) {
            Plugin.getInstance().getLogger().warning("Could not update the ItemStorage");
            e.printStackTrace();
        }

        return newItem;
    }
}
