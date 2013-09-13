package com.geNAZt.RegionShop.Database.Model;

import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.ItemMeta;
import com.geNAZt.RegionShop.Database.Table.ItemMetaID;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 01.09.13
 */
public class Item {
    private static boolean hasMeta(ItemStack itemStack) {
        ItemMeta itemMeta = Database.getServer().find(ItemMeta.class).
                where().
                    eq("item_id", itemStack.getTypeId()).
                    eq("data_value", itemStack.getData().getData()).
                findUnique();

        return !(itemMeta == null);
    }

    private static void createMeta(ItemStack itemStack) {
        ItemMeta itemMeta = new ItemMeta();
        itemMeta.setId(new ItemMetaID(itemStack.getTypeId(), itemStack.getData().getData()));
        itemMeta.setMaxStackSize(itemStack.getType().getMaxStackSize());
        itemMeta.setMaxDurability(itemStack.getType().getMaxDurability());

        Database.getServer().save(itemMeta);
    }
}
