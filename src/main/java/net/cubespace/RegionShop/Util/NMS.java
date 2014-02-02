package net.cubespace.RegionShop.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NMS {
    private static int nextId = 0;

    public static void safeGuard(Item item) {
        rename(item.getItemStack());
        item.setPickupDelay(Integer.MAX_VALUE);
    }

    private static void rename(ItemStack iStack) {
        ItemMeta meta = iStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "RegionShop " + iStack.getType().toString() + " " + nextId++);
        iStack.setItemMeta(meta);
    }
}
