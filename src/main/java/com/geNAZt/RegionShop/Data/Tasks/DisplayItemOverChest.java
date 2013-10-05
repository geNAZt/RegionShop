package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Table.Chest;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 04.10.13
 */
public class DisplayItemOverChest extends BukkitRunnable {
    @Override
    public void run() {
        List<Chest> chestList  = Database.getServer().find(Chest.class).findList();

        for(final Chest chest : chestList) {
            boolean found = false;
            for (Entity ent : Bukkit.getWorld(chest.getWorld()).getEntities()) {
                if(ent.getLocation().getBlockY() == chest.getChestY()+1 && ent.getLocation().getBlockX() == chest.getChestX() && ent.getLocation().getBlockZ() == chest.getChestZ()) {
                    found = true;
                }
            }

            if(!found) {
                final ItemStack itemStack = Item.fromDBItem(chest.getItemStorage().getItems().iterator().next());
                itemStack.setAmount(1);

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RegionShopPlugin.getInstance(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        org.bukkit.entity.Item droppedItem = Bukkit.getWorld(chest.getWorld()).dropItem(new Location(Bukkit.getWorld(chest.getWorld()), (double) chest.getChestX() + 0.5, (double)chest.getChestY() + 1.2, (double)chest.getChestZ() + 0.5), itemStack);
                        droppedItem.setVelocity(new Vector(0, 0.1, 0));
                        NMS.safeGuard(droppedItem);
                    }
                });
            }
        }
    }
}
