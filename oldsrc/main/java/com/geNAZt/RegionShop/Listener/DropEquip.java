package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Data.Storages.DropStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class DropEquip extends Listener {
    public void execute(PlayerDropItemEvent event) {
        if (DropStorage.has(event.getPlayer())) {
            Region region = DropStorage.get(event.getPlayer());

            ItemStack droppedItem = event.getItemDrop().getItemStack();

            if(Equip.equip(droppedItem, event.getPlayer(), region) == 0) {
                event.getItemDrop().remove();
            } else {
                event.getItemDrop().remove();

                String itemName;
                if (droppedItem.getItemMeta().hasDisplayName()) {
                    itemName = ItemName.getDataName(droppedItem) + droppedItem.getItemMeta().getDisplayName();
                } else {
                    itemName = ItemName.getDataName(droppedItem) + droppedItem.getType().toString();
                }

                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
            }
        }
    }
}
