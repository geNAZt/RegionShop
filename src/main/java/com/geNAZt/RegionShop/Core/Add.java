package com.geNAZt.RegionShop.Core;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Config.Sub.Group;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Model.Transaction;
import com.geNAZt.RegionShop.Database.Table.Enchantment;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.07.13
 */
public class Add {
    public static Integer add(ItemStack item, Player player, Region shop, Float sell, Float buy, Integer amount) {
        //Ask Database for this Item
        List<Items> dbItem = Database.getServer().find(Items.class).
                where().
                    conjunction().
                        eq("itemStorage.name", shop.getItemStorage().getName()).
                        eq("meta.id.itemID", item.getType().getId()).
                        eq("meta.id.dataValue", item.getData().getData()).
                        eq("durability", item.getDurability()).
                        eq("owner", player.getName()).
                        eq("customName", (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null).
                    endJunction().
                findList();

        Group group = ConfigManager.main.getGroup(shop.getItemStorage().getSetting());

        //Check if there is place in the Storage
        if(shop.getItemStorage().getItemAmount() + item.getAmount() >= group.Storage) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Add_FullStorage);

            return -1;
        }

        //Check if item is already in the Database
        if (dbItem == null || dbItem.isEmpty()) {
            //It is new. Convert it into the Database
            Item.toDBItem(item, InRegion.get(player), player.getName(), buy, sell, amount);

            //Save a Transaction for it
            Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.ADD, shop.getName(), player.getWorld().getName(), player.getName(), item.getTypeId(), item.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

            return 0;
        } else {
            boolean found = false;
            Integer itemID = 0;

            for(Items it : dbItem) {
                //Check if enchantments are the same
                Set<Enchantment> enchantments = it.getEnchantments();
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
                Item.toDBItem(item, InRegion.get(player), player.getName(), buy, sell, amount);

                Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.ADD, shop.getName(), player.getWorld().getName(), player.getName(), item.getTypeId(), item.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

                return 0;
            }
        }
    }
}
