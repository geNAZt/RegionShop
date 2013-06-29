package com.geNAZt.RegionShop.Filter;

import com.geNAZt.RegionShop.Model.ShopItems;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.06.13
 */
public abstract class Filter {
    public abstract boolean checkItem(ShopItems shopItem, ItemStack item);
    public abstract String parse(String pattern);
}
