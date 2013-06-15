package com.geNAZt.RegionShop.Converter;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;

import com.geNAZt.RegionShop.Converter.ChestShop.ConvertCommandExecutor;
import com.geNAZt.RegionShop.Converter.ChestShop.ConvertStorage;
import com.geNAZt.RegionShop.Converter.ChestShop.Listener.ChestShopTransaction;
import com.geNAZt.RegionShop.Converter.ChestShop.Listener.PlayerQuit;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
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
    protected static RegionShopPlugin plugin;

    public ChestShopConverter(RegionShopPlugin pl) {
        plugin = pl;

        ConvertStorage.init(pl);

        plugin.getCommand("convert").setExecutor(new ConvertCommandExecutor());

        plugin.getServer().getPluginManager().registerEvents(new ChestShopTransaction(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerQuit(), plugin);
    }

    public static void convertInventory(Inventory inv, ItemStack find, Player p, String owner, Integer buy, Integer sell, Integer amount) {
        ItemStack[] invItems = inv.getContents();
        boolean first = true;
        Integer converted = 0;

        ShopItems sItem = null;

        for(ItemStack item: invItems) {
            if(item == null) {
                continue;
            }

            if (item.getType().toString().contains(find.getType().toString())) {
                converted += item.getAmount();

                if(first) {
                    first = false;

                    sItem = plugin.getDatabase().find(ShopItems.class).
                            where().
                                conjunction().
                                    eq("world", p.getWorld().getName()).
                                    eq("region", PlayerStorage.getPlayer(p)).
                                    eq("item_id", item.getType().getId()).
                                    eq("data_id", item.getData().getData()).
                                    eq("durability", item.getDurability()).
                                    eq("owner", owner).
                                    eq("custom_name", (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null).
                                endJunction().
                            findUnique();

                    if(sItem == null) {
                        sItem = ItemConverter.toDBItem(item, p.getWorld(), owner, PlayerStorage.getPlayer(p), buy, sell, amount);
                        inv.remove(item);
                    } else {
                        sItem.setCurrentAmount(sItem.getCurrentAmount() + item.getAmount());
                        plugin.getDatabase().update(sItem);

                        inv.remove(item);
                    }
                } else {
                    sItem.setCurrentAmount(sItem.getCurrentAmount() + item.getAmount());
                    plugin.getDatabase().update(sItem);

                    inv.remove(item);
                }
            }
        }

        p.sendMessage(Chat.getPrefix() + "Converted " + converted + " of " + ItemName.nicer(find.getType().toString()));
    }
}
