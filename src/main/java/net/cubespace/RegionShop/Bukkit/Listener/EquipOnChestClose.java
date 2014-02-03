package net.cubespace.RegionShop.Bukkit.Listener;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.ChestRepository;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ListIterator;

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
            net.cubespace.RegionShop.Database.Table.Chest chest1 = ChestRepository.get(chest.getBlock(), event.getPlayer().getWorld());

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

                if(itemStack.getTypeId() == item.getMeta().getItemID() && itemStack.getData().getData() == item.getMeta().getDataValue()) {
                    itemAmount += itemStack.getAmount();
                }
            }

            try {
                item.setCurrentAmount(itemAmount);
                Database.getDAO(Items.class).update(item);

                item.getItemStorage().setItemAmount(itemAmount);
                Database.getDAO(ItemStorage.class).update(item.getItemStorage());
            } catch (SQLException e) {
                Logger.error("Could not update Item", e);
            }
        }
    }
}
