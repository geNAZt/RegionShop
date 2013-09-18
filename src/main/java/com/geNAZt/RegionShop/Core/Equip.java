package com.geNAZt.RegionShop.Core;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Config.Sub.Group;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Model.Transaction;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 20.07.13
 */
public class Equip {
    public static Integer equip(ItemStack item, Player player, Region shop) {
        Group group = ConfigManager.main.getGroup(shop.getItemStorage().getSetting());

        //Check if there is place in the Storage
        if(shop.getItemStorage().getItemAmount() + item.getAmount() >= group.Storage) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Add_FullStorage);

            return -1;
        }

        Items dbItem = Database.getServer().find(Items.class).
                where().
                    conjunction().
                        eq("itemStorage", shop.getItemStorage()).
                        eq("meta.id.itemID", item.getType().getId()).
                        eq("meta.id.dataValue", item.getData().getData()).
                        eq("durability", item.getDurability()).
                        eq("owner", player.getName()).
                        eq("customName", (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null).
                    endJunction().
                findUnique();

        if (dbItem != null) {
            dbItem.setCurrentAmount(dbItem.getCurrentAmount() + item.getAmount());

            Database.getServer().update(dbItem);

            Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.EQUIP, shop.getName(), shop.getWorld(), dbItem.getOwner(), dbItem.getMeta().getId().getItemID(), item.getAmount(), dbItem.getSell().doubleValue(), dbItem.getBuy().doubleValue(), dbItem.getUnitAmount());

            return 0;
        } else {
            dbItem = Item.toDBItem(item, shop, player.getName(), 0.0F, 0.0F, 0);

            Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.EQUIP, shop.getName(), player.getWorld().getName(), player.getName(), dbItem.getMeta().getId().getItemID(), item.getAmount(), 0.0, 0.0, 0);

            return dbItem.getId();
        }
    }
}
