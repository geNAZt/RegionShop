package com.geNAZt.RegionShop.ServerShop;

import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 17.06.13
 */
public class PriceStorage {
    private static final ConcurrentHashMap<ItemStack, Price> itemPrices = new ConcurrentHashMap<ItemStack, Price>();

    public static synchronized void add(ItemStack itemStack, Price price) {
        if(itemPrices.containsKey(itemStack)) {
            itemPrices.remove(itemStack);
        }

        itemPrices.put(itemStack, price);
    }

    public static synchronized ConcurrentHashMap<ItemStack, Price> getAll() {
        return itemPrices;
    }

    public static synchronized Price get(ItemStack itemStack) {
        if(itemPrices.containsKey(itemStack)) {
            return itemPrices.get(itemStack);
        }

        return null;
    }
}
