package net.cubespace.RegionShop.Core;

import com.j256.ormlite.stmt.QueryBuilder;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Config.Files.Sub.Group;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.ItemStorageHolder;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Repository.TransactionRepository;
import net.cubespace.RegionShop.Database.Table.ItemMeta;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Transaction;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

public class Equip {
    public static Integer equip(ItemStack item, Player player, ItemStorageHolder shop) {
        Group group = ConfigManager.groups.getGroup(shop.getItemStorage().getSetting());

        //Check if there is place in the Storage
        if(shop.getItemStorage().getItemAmount() + item.getAmount() >= group.Storage) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Add_FullStorage);

            return -1;
        }

        //Ask Database for this Item
        final Items dbItem;
        try {
            QueryBuilder<ItemMeta, Integer> itemMetaQB = Database.getDAO(ItemMeta.class).queryBuilder();
            itemMetaQB.where().
                    eq("itemID", item.getType().getId()).
                    and().
                    eq("dataValue", item.getData().getData());

            dbItem = Database.getDAO(Items.class).queryBuilder().
                    join(itemMetaQB).
                    where().
                    eq("itemstorage_id", shop.getItemStorage().getId()).
                    and().
                    eq("durability", item.getDurability()).
                    and().
                    eq("owner", player.getName()).
                    and().
                    eq("customName", (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : "").
                    queryForFirst();
        } catch (SQLException e) {
            Plugin.getInstance().getLogger().severe("Could not get Items");
            e.printStackTrace();
            return -1;
        }

        if (dbItem != null) {
            dbItem.setCurrentAmount(dbItem.getCurrentAmount() + item.getAmount());
            dbItem.getItemStorage().setItemAmount(dbItem.getItemStorage().getItemAmount() + item.getAmount());

            Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        Database.getDAO(ItemStorage.class).update(dbItem.getItemStorage());
                        Database.getDAO(Items.class).update(dbItem);
                    } catch (SQLException e) {
                        Logger.error("Could not update Item", e);
                    }
                }
            });

            TransactionRepository.generateTransaction(player, Transaction.TransactionType.EQUIP, shop.getName(), shop.getWorld(), dbItem.getOwner(), dbItem.getMeta().getItemID(), item.getAmount(), dbItem.getSell().doubleValue(), dbItem.getBuy().doubleValue(), dbItem.getUnitAmount());

            return 0;
        } else {
            final Items newDbItem = ItemRepository.toDBItem(item, shop, player.getName(), 0.0F, 0.0F, 0);

            newDbItem.getItemStorage().setItemAmount(newDbItem.getItemStorage().getItemAmount() + item.getAmount());

            Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        Database.getDAO(ItemStorage.class).update(newDbItem.getItemStorage());
                    } catch (SQLException e) {
                        Logger.error("Could not update ItemStorage", e);
                    }
                }
            });

            TransactionRepository.generateTransaction(player, Transaction.TransactionType.EQUIP, shop.getName(), player.getWorld().getName(), player.getName(), newDbItem.getMeta().getItemID(), item.getAmount(), 0.0, 0.0, 0);

            return newDbItem.getId();
        }
    }
}
