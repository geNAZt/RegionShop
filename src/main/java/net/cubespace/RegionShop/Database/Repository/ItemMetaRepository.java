package net.cubespace.RegionShop.Database.Repository;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.ItemMeta;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class ItemMetaRepository {
    private static boolean isStored(ItemStack itemStack) {
        ItemMeta itemMeta = null;
        try {
            itemMeta = Database.getDAO(ItemMeta.class).queryBuilder().
                    where().
                        eq("item_id", itemStack.getTypeId()).
                        and().
                        eq("data_value", itemStack.getData().getData()).
                    queryForFirst();
        } catch (SQLException e) { }

        return !(itemMeta == null);
    }

    private static void insert(ItemStack itemStack) {
        ItemMeta itemMeta = new ItemMeta();
        itemMeta.setItemID(itemStack.getTypeId());
        itemMeta.setDataValue(itemStack.getData().getData());

        try {
            Database.getDAO(ItemMeta.class).create(itemMeta);
        } catch (SQLException e) {
            Plugin.getInstance().getLogger().info("Could not save ItemMeta");
            e.printStackTrace();
        }
    }
}
