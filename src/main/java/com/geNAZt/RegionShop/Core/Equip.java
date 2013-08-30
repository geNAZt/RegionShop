package com.geNAZt.RegionShop.Core;

import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Transaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 20.07.13
 */
public class Equip {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin plugin) {
        Equip.plugin = plugin;
    }

    public static Integer equip(ItemStack item, Player player, Region shop) {
        ShopItems dbItem = plugin.getDatabase().find(ShopItems.class).
                where().
                    conjunction().
                        eq("world", player.getWorld().getName()).
                        eq("region", shop.getItemStorage()).
                        eq("item_id", item.getType().getId()).
                        eq("data_id", item.getData().getData()).
                        eq("durability", item.getDurability()).
                        eq("owner", player.getName()).
                        eq("custom_name", (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null).
                    endJunction().
                findUnique();

        if (dbItem != null) {
            dbItem.setCurrentAmount(dbItem.getCurrentAmount() + item.getAmount());

            plugin.getDatabase().update(dbItem);

            Transaction.generateTransaction(player, ShopTransaction.TransactionType.EQUIP, shop.getName(), dbItem.getWorld(), dbItem.getOwner(), dbItem.getItemID(), item.getAmount(), dbItem.getSell().doubleValue(), dbItem.getBuy().doubleValue(), dbItem.getUnitAmount());

            return 0;
        } else {
            dbItem = ItemConverter.toDBItem(item, player.getWorld(), player.getName(), shop.getItemStorage(), 0, 0, 0);

            Transaction.generateTransaction(player, ShopTransaction.TransactionType.EQUIP, shop.getName(), player.getWorld().getName(), player.getName(), dbItem.getItemID(), item.getAmount(), 0.0, 0.0, 0);

            return dbItem.getId();
        }
    }
}
