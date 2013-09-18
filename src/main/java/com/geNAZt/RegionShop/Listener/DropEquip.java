package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Core.Equip;
import com.geNAZt.RegionShop.Data.Storage.Drop;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class DropEquip implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (Drop.has(event.getPlayer())) {
            Region region = Drop.get(event.getPlayer());

            ItemStack droppedItem = event.getItemDrop().getItemStack();

            Integer mode;
            if((mode = Equip.equip(droppedItem, event.getPlayer(), region)) == 0) {
                event.getItemDrop().remove();
            } else {
                if(mode != -1) {
                    event.getItemDrop().remove();

                    String itemName;
                    if (droppedItem.getItemMeta().hasDisplayName()) {
                        itemName = ItemName.getDataName(droppedItem) + droppedItem.getItemMeta().getDisplayName();
                    } else {
                        itemName = ItemName.getDataName(droppedItem) + droppedItem.getType().toString();
                    }

                    event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Equip_Add_Item.replace("%item", ItemName.nicer(itemName)));
                }
            }
        }
    }
}
