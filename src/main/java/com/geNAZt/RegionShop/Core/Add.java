package com.geNAZt.RegionShop.Core;

import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Model.ShopItemEnchantments;
import com.geNAZt.RegionShop.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Transaction;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.07.13
 */
public class Add {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin plugin) {
        Add.plugin = plugin;
    }

    public static Integer add(ItemStack item, Player player, Region shop, Integer sell, Integer buy, Integer amount) {
        //Ask Database for this Item
        List<ShopItems> dbItem = plugin.getDatabase().find(ShopItems.class).
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
                findList();

        //Check if item is already in the Database
        if (dbItem == null || dbItem.isEmpty()) {
            //It is new. Convert it into the Database
            ItemConverter.toDBItem(item, player.getWorld(), player.getName(), shop.getItemStorage(), buy, sell, amount);

            Transaction.generateTransaction(player, ShopTransaction.TransactionType.ADD, shop.getName(), player.getWorld().getName(), player.getName(), item.getTypeId(), item.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

            return 0;
        } else {
            boolean found = false;
            Integer itemID = 0;

            for(ShopItems it : dbItem) {
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
            }
        }
    }
}
