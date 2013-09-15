package com.geNAZt.RegionShop.Interface.CLI.Commands.SearchFilter;

import com.geNAZt.RegionShop.Database.Table.Items;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.06.13
 */
public abstract class Filter {
    public abstract boolean checkItem(Items shopItem, ItemStack item);
    public abstract String parse(String pattern);
}
