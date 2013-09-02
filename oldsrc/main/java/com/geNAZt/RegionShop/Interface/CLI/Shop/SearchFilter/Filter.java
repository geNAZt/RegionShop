package com.geNAZt.RegionShop.Interface.CLI.Shop.SearchFilter;

import com.geNAZt.RegionShop.Database.Database.Model.ShopItems;
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
