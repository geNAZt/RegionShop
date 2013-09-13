package com.geNAZt.RegionShop.Core;

import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.07.13
 */
public class Add {
    public static Integer add(ItemStack item, Player player, Region shop, Integer sell, Integer buy, Integer amount) {
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

        //Check if item is already in the Database
        if (dbItem == null || dbItem.isEmpty()) {
            //It is new. Convert it into the Database
            Item.toDBItem(item, InRegion.get(player), player.getName(), buy, sell, amount);

            //Transaction.generateTransaction(player, ShopTransaction.TransactionType.ADD, shop.getName(), player.getWorld().getName(), player.getName(), item.getTypeId(), item.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

            return 0;
        } else {
            boolean found = false;
            Integer itemID = 0;

            /*for(Items it : dbItem) {
                //Check if enchantments are the same
                List<ShopItemEnchantments> enchantments = plugin.getDatabase().find(ShopItemEnchantments.class).
                        where().
                            eq("shop_item_id", it.getId()).
                        findList();

                Map<Enchantment, Integer> enchOnItem = item.getEnchantments();

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
                    for(Map.Entry<Enchantment, Integer> ench : enchOnItem.entrySet()) {
                        for(ShopItemEnchantments enchI : enchantments) {
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
                ItemConverter.toDBItem(item, player.getWorld(), player.getName(), shop.getItemStorage(), buy, sell, amount);

                Transaction.generateTransaction(player, ShopTransaction.TransactionType.ADD, shop.getName(), player.getWorld().getName(), player.getName(), item.getTypeId(), item.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

                return 0;
            }*/
            return 0;
        }
    }
}
