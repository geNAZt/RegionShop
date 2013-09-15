package com.geNAZt.RegionShop.Converter;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Converter.ChestShop.ConvertCommandExecutor;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class ChestShopConverter {
    private static RegionShopPlugin plugin;

    public ChestShopConverter(RegionShopPlugin pl) {
        plugin = pl;

        plugin.getCommand("convert").setExecutor(new ConvertCommandExecutor());
    }

    public static void convertInventory(Inventory inv, ItemStack find, Player p, String owner, Float buy, Float sell, Integer amount) {
        ItemStack[] invItems = inv.getContents();
        boolean first = true;
        Integer converted = 0;

        Items sItem = null;

        for(ItemStack item: invItems) {
            if(item == null) {
                continue;
            }

            if (item.getType().toString().contains(find.getType().toString())) {
                converted += item.getAmount();

                if(first) {
                    first = false;

                    sItem = Database.getServer().find(Items.class).
                            where().
                                conjunction().
                                    eq("itemStorage", InRegion.get(p).getItemStorage()).
                                    eq("meta.id.itemID", item.getType().getId()).
                                    eq("meta.id.dataValue", item.getData().getData()).
                                    eq("durability", item.getDurability()).
                                    eq("owner", owner).
                                    eq("customName", (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null).
                                endJunction().
                            findUnique();

                    if(sItem == null) {
                        sItem = Item.toDBItem(item, InRegion.get(p), owner, buy, sell, amount);
                        inv.remove(item);
                    } else {
                        sItem.setCurrentAmount(sItem.getCurrentAmount() + item.getAmount());
                        Database.getServer().update(sItem);

                        inv.remove(item);
                    }
                } else {
                    sItem.setCurrentAmount(sItem.getCurrentAmount() + item.getAmount());
                    Database.getServer().update(sItem);

                    inv.remove(item);
                }
            }
        }

        p.sendMessage(ConfigManager.main.Chat_prefix + "Converted " + converted + " of " + ItemName.nicer(find.getType().toString()) + " for " + owner);
    }
}
