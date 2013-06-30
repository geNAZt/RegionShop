package com.geNAZt.RegionShop.ServerShop;

import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 17.06.13
 */
public class PriceStorage {
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, Price>> itemPrices = new ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, Price>>();

    public static synchronized void add(String region, ItemStack itemStack, Price price) {
        if(itemPrices.containsKey(region)) {
            ConcurrentHashMap<ItemStack, Price> itemsInRegion = itemPrices.get(region);
            if(itemsInRegion.containsKey(itemStack)) {
                itemsInRegion.remove(itemStack);
                itemPrices.put(region, itemsInRegion);
            }
        } else {
            itemPrices.put(region, new ConcurrentHashMap<ItemStack, Price>());
        }

        ConcurrentHashMap<ItemStack, Price> itemsInRegion2 = itemPrices.get(region);
        itemsInRegion2.put(itemStack, price);
        itemPrices.put(region, itemsInRegion2);
    }

    public static synchronized ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, Price>> getAll() {
        return itemPrices;
    }

    public static synchronized ConcurrentHashMap<ItemStack, Price> getRegion(String region) {
        if(itemPrices.containsKey(region)) {
            return itemPrices.get(region);
        } else {
            return null;
        }
    }

    public static synchronized Price get(String region, ItemStack itemStack) {
        if(itemPrices.containsKey(region)) {
            ConcurrentHashMap<ItemStack, Price> itemsInRegion = itemPrices.get(region);
            if(itemsInRegion.containsKey(itemStack)) {
                return itemsInRegion.get(itemStack);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
