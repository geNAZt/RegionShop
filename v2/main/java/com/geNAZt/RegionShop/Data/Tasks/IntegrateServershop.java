package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Config.Sub.Item;
import com.geNAZt.RegionShop.Config.Sub.ServerShop;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.CustomerSign;
import com.geNAZt.RegionShop.Database.Table.ItemStorage;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.09.13
 */
public class IntegrateServershop extends BukkitRunnable {
    @Override
    public void run() {
        for(ServerShop shop : ConfigManager.servershop.ServerShops) {
            //Check if Region is listed as ServerShop
            Region region = Database.getServer().find(Region.class).
                    where().
                        eq("region", shop.Region).
                    findUnique();

            if(region == null) {
                Logger.warn("Servershop " + shop.Region + " could not be found");
                continue;
            }

            ItemStorage itemStorage = region.getItemStorage();
            if(!itemStorage.isServershop()) {
                itemStorage.setServershop(true);

                Database.getServer().update(itemStorage);
            }

            //Check if all Items are listed
            Set<Items> itemsSet = itemStorage.getItems();

            for(Item item : shop.Items) {
                boolean found = false;
                for(Items items : itemsSet) {
                    if(items.getMeta().getId().getItemID().equals(item.itemID) && items.getMeta().getId().getDataValue().equals(item.dataValue.byteValue())) {
                        found = true;
                        break;
                    }
                }

                //Not found => create
                if(!found) {
                    ItemStack itemStack = new ItemStack(item.itemID);
                    if(item.dataValue > 0) {
                        if(item.dataValue < 128) {
                            itemStack.getData().setData(item.dataValue.byteValue());
                        } else {
                            itemStack.setDurability(item.dataValue);
                        }
                    }

                    Items newItems = com.geNAZt.RegionShop.Database.Model.Item.toDBItem(itemStack, region, "Servershop", item.buy, item.sell, 1);
                    newItems.setCurrentAmount(99999);
                    Database.getServer().update(newItems);
                }
            }

            for(Items items : itemsSet) {
                boolean found = false;
                for(Item item : shop.Items) {
                    if(items.getMeta().getId().getItemID().equals(item.itemID) && items.getMeta().getId().getDataValue().equals(item.dataValue.byteValue())) {
                        found = true;
                        break;
                    }
                }

                //Not found => delete
                if(!found) {
                    CustomerSign customerSign = Database.getServer().find(CustomerSign.class).where().eq("item", items).findUnique();
                    if(customerSign != null) {
                        Bukkit.getWorld(customerSign.getRegion().getWorld()).getBlockAt(customerSign.getX(), customerSign.getY(), customerSign.getZ()).setType(Material.AIR);

                        for (final Entity ent : Bukkit.getWorld(customerSign.getRegion().getWorld()).getEntities()) {
                            //Get the location of this Entity
                            Location entLocation = ent.getLocation();
                            if (entLocation.getBlockZ() == customerSign.getZ() && entLocation.getBlockY() == customerSign.getY() - 1 && entLocation.getBlockX() == customerSign.getX()) {
                                ent.remove();
                            }
                        }

                        Database.getServer().delete(customerSign);
                    }

                    Database.getServer().delete(items);
                }
            }
        }
    }
}
