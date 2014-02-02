package net.cubespace.RegionShop.Interface.CLI.Commands.SearchFilter;

import net.cubespace.RegionShop.Database.Table.Items;
import org.bukkit.inventory.ItemStack;

public abstract class Filter {
    public abstract boolean checkItem(Items shopItem, ItemStack item);
    public abstract String parse(String pattern);
}
