package net.cubespace.RegionShop.Core;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.QueryBuilder;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Config.Files.Sub.Group;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.ItemStorageHolder;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Repository.TransactionRepository;
import net.cubespace.RegionShop.Database.Table.Enchantment;
import net.cubespace.RegionShop.Database.Table.ItemMeta;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Transaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Add {
    public static Integer add(ItemStack item, Player player, ItemStorageHolder shop, Float sell, Float buy, Integer amount) {
        //Ask Database for this Item
        List<Items> dbItem;
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
                    query();
        } catch (SQLException e) {
            Plugin.getInstance().getLogger().severe("Could not get Items");
            e.printStackTrace();
            return -1;
        }

        Group group = ConfigManager.groups.getGroup(shop.getItemStorage().getSetting());

        //Check if there is place in the Storage
        if(shop.getItemStorage().getItemAmount() + item.getAmount() >= group.Storage) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Add_FullStorage);

            return -1;
        }

        //Check if item is already in the Database
        if (dbItem == null || dbItem.isEmpty()) {
            //It is new. Convert it into the Database
            ItemRepository.toDBItem(item, shop, player.getName(), buy, sell, amount);

            //Save a Transaction for it
            TransactionRepository.generateTransaction(player, Transaction.TransactionType.ADD, shop.getName(), player.getWorld().getName(), player.getName(), item.getTypeId(), item.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

            return 0;
        } else {
            boolean found = false;
            Integer itemID = 0;

            for(Items it : dbItem) {
                //Check if enchantments are the same
                ForeignCollection<Enchantment> enchantments = it.getEnchantments();
                Map<org.bukkit.enchantments.Enchantment, Integer> enchOnItem = item.getEnchantments();

                if((enchantments == null || enchantments.isEmpty()) && (enchOnItem == null || enchOnItem.isEmpty())) {
                    found = true;
                    itemID = it.getId();
                    break;
                } else {
                    if(enchantments == null || enchantments.isEmpty()) {
                        continue;
                    }

                    if (enchOnItem == null || enchOnItem.isEmpty()) {
                        continue;
                    }

                    Integer foundEnchs = 0;
                    for(Map.Entry<org.bukkit.enchantments.Enchantment, Integer> ench : enchOnItem.entrySet()) {
                        for(Enchantment enchI : enchantments) {
                            if(enchI.getEnchId().equals(ench.getKey().getId()) && enchI.getEnchLvl().equals(ench.getValue())) {
                                foundEnchs++;
                            }
                        }
                    }

                    if(foundEnchs.equals(enchOnItem.size()) && enchantments.size() == enchOnItem.size()) {
                        itemID = it.getId();
                        found = true;
                        break;
                    }
                }
            }

            if(found) {
                //Item is already added
                return itemID;
            } else {
                //It is new. Convert it into the Database
                ItemRepository.toDBItem(item, shop, player.getName(), buy, sell, amount);

                TransactionRepository.generateTransaction(player, Transaction.TransactionType.ADD, shop.getName(), player.getWorld().getName(), player.getName(), item.getTypeId(), item.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

                return 0;
            }
        }
    }
}
