package net.cubespace.RegionShop.Database.Repository;

import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.ItemMeta;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class ItemMetaRepository {
    public static boolean isStored(ItemStack itemStack) {
        ItemMeta itemMeta = null;
        try {
            itemMeta = Database.getDAO(ItemMeta.class).queryBuilder().
                    where().
                        eq("itemID", itemStack.getTypeId()).
                        and().
                        eq("dataValue", itemStack.getData().getData()).
                    queryForFirst();
        } catch (SQLException e) { }

        return !(itemMeta == null);
    }

    public static void insert(ItemStack itemStack) {
        ItemMeta itemMeta = new ItemMeta();
        itemMeta.setItemID(itemStack.getTypeId());
        itemMeta.setDataValue(itemStack.getData().getData());
        itemMeta.setMaxStackSize(itemStack.getMaxStackSize());

        try {
            Database.getDAO(ItemMeta.class).create(itemMeta);
        } catch (SQLException e) {
            Logger.warn("Could not insert new ItemMeta", e);
        }
    }

    public static ItemMeta get(ItemStack itemStack) {
        try {
            return Database.getDAO(ItemMeta.class).queryBuilder().
                    where().
                    eq("itemID", itemStack.getTypeId()).
                    and().
                    eq("dataValue", itemStack.getData().getData()).
                    queryForFirst();
        } catch (SQLException e) {
            return null;
        }
    }
}
