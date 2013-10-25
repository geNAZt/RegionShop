package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Core.Add;
import com.geNAZt.RegionShop.Core.Equip;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Items;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ListIterator;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 03.10.13
 */
public class EquipOnChestClose implements Listener {
    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {
        //Check if Event is in an enabled world
        if(!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
            return;
        }

        InventoryHolder inventoryHolder = event.getInventory().getHolder();

        if(inventoryHolder instanceof Chest) {
            Chest chest = (Chest) inventoryHolder;

            //Check if Chest is a ChestShop
            com.geNAZt.RegionShop.Database.Table.Chest chest1 = Database.getServer().find(com.geNAZt.RegionShop.Database.Table.Chest.class).
                    where().
                        eq("world", event.getPlayer().getWorld().getName()).
                        eq("chestY", chest.getY()).
                        eq("chestX", chest.getX()).
                        eq("chestZ", chest.getZ()).
                    findUnique();

            if(chest1 == null) {
                return;
            }

            //Get the Item inside the ChestShop
            Items item = chest1.getItemStorage().getItems().iterator().next();

            //Store the Items
            Integer itemAmount = 0;
            ListIterator<ItemStack> itemStackListIterator = inventoryHolder.getInventory().iterator();
            while(itemStackListIterator.hasNext()) {
                ItemStack itemStack = itemStackListIterator.next();

                if(itemStack == null) continue;

                if(itemStack.getTypeId() == item.getMeta().getId().getItemID() && itemStack.getData().getData() == item.getMeta().getId().getDataValue()) {
                    itemAmount += itemStack.getAmount();
                }
            }

            item.setCurrentAmount(itemAmount);
            Database.getServer().update(item);

            item.getItemStorage().setItemAmount(itemAmount);
            Database.getServer().update(item.getItemStorage());
        }
    }
}
