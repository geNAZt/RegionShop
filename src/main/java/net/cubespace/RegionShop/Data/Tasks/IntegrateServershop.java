package net.cubespace.RegionShop.Data.Tasks;

import com.j256.ormlite.dao.ForeignCollection;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Config.Files.Sub.Item;
import net.cubespace.RegionShop.Config.Files.Sub.ServerShop;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Table.CustomerSign;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class IntegrateServershop extends BukkitRunnable {
    @Override
    public void run() {
        for(ServerShop shop : ConfigManager.servershop.ServerShops) {
            //Check if Region is listed as ServerShop
            Region region = null;
            try {
                region = Database.getDAO(Region.class).queryBuilder().
                        where().
                            eq("region", shop.Region).
                        queryForFirst();
            } catch (SQLException e) {
                Logger.error("Could not get Region", e);
            }

            if(region == null) {
                Logger.warn("Servershop " + shop.Region + " could not be found");
                continue;
            }

            ItemStorage itemStorage = region.getItemStorage();
            if(!itemStorage.isServershop()) {
                itemStorage.setServershop(true);

                try {
                    Database.getDAO(ItemStorage.class).update(itemStorage);
                } catch (SQLException e) {
                    Logger.warn("Could not set Servershop Status", e);
                    continue;
                }
            }

            //Check if all Items are listed
            ForeignCollection<Items> itemsSet = itemStorage.getItems();

            for(Item item : shop.Items) {
                boolean found = false;
                for(Items items : itemsSet) {
                    if(items.getMeta().getItemID().equals(item.itemID) && items.getMeta().getDataValue().equals(item.dataValue.byteValue())) {
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

                    Items newItems = ItemRepository.toDBItem(itemStack, region, "Servershop", item.buy, item.sell, 1);
                    newItems.setCurrentAmount(99999);
                    try {
                        Database.getDAO(Items.class).update(newItems);
                    } catch (SQLException e) {
                        Logger.warn("Could not set unlimited Amount", e);
                    }
                }
            }

            for(Items items : itemsSet) {
                boolean found = false;
                for(Item item : shop.Items) {
                    if(items.getMeta().getItemID().equals(item.itemID) && items.getMeta().getDataValue().equals(item.dataValue.byteValue())) {
                        found = true;
                        break;
                    }
                }

                //Not found => delete
                if(!found) {
                    CustomerSign customerSign = null;
                    try {
                        customerSign = Database.getDAO(CustomerSign.class).queryBuilder().where().eq("item_id", items.getId()).queryForFirst();
                    } catch (SQLException e) {
                        Logger.error("Could not get old Item Sign", e);
                    }

                    if(customerSign != null) {
                        Bukkit.getWorld(customerSign.getRegion().getWorld()).getBlockAt(customerSign.getX(), customerSign.getY(), customerSign.getZ()).setType(Material.AIR);

                        for (final Entity ent : Bukkit.getWorld(customerSign.getRegion().getWorld()).getEntities()) {
                            //Get the location of this Entity
                            Location entLocation = ent.getLocation();
                            if (entLocation.getBlockZ() == customerSign.getZ() && entLocation.getBlockY() == customerSign.getY() - 1 && entLocation.getBlockX() == customerSign.getX()) {
                                ent.remove();
                            }
                        }

                        try {
                            Database.getDAO(CustomerSign.class).delete(customerSign);
                        } catch (SQLException e) {
                            Logger.error("Could not delete the CustomerSign", e);
                        }
                    }

                    try {
                        Database.getDAO(Items.class).delete(items);
                    } catch (SQLException e) {
                        Logger.error("Could not delete Item", e);
                    }
                }
            }
        }
    }
}
