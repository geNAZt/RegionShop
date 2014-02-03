package net.cubespace.RegionShop.Bukkit.Listener;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Repository.ChestRepository;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Table.Items;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class RestoreOnChestOpen implements Listener {
    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
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

            ItemStack itemStack = ItemRepository.fromDBItem(item);
            itemStack.setAmount(item.getCurrentAmount());

            event.getInventory().clear();
            event.getInventory().addItem(itemStack);
        }
    }
}
