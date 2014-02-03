package net.cubespace.RegionShop.Bukkit.Listener;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Core.Equip;
import net.cubespace.RegionShop.Data.Storage.Drop;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Util.ItemName;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class DropEquip implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        //Check if Event is in an enabled world
        if(!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
            return;
        }

        if (Drop.has(event.getPlayer())) {
            Region region = Drop.get(event.getPlayer());

            ItemStack droppedItem = event.getItemDrop().getItemStack();

            Integer mode;
            if((mode = Equip.equip(droppedItem, event.getPlayer(), region)) == 0) {
                event.getItemDrop().remove();
            } else {
                if(mode != -1) {
                    event.getItemDrop().remove();

                    String dataName = ItemName.getDataName(droppedItem);
                    String niceItemName;
                    if(dataName.endsWith(" ")) {
                        niceItemName = dataName + ItemName.nicer(droppedItem.getType().toString());
                    } else if(!dataName.equals("")) {
                        niceItemName = dataName;
                    } else {
                        niceItemName = ItemName.nicer(droppedItem.getType().toString());
                    }

                    if (droppedItem.getItemMeta().hasDisplayName()) {
                        niceItemName += "(" + droppedItem.getItemMeta().getDisplayName() + ")";
                    }

                    event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Equip_Add_Item.replace("%item", ItemName.nicer(niceItemName)));
                }
            }
        }
    }
}
