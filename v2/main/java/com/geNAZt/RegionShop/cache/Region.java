package com.geNAZt.RegionShop.cache;

import com.geNAZt.RegionShop.Database.Database;
import org.bukkit.Location;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.10.13
 */
public class Region {
    private static List<com.geNAZt.RegionShop.Database.Table.Region> regionList = new CopyOnWriteArrayList<com.geNAZt.RegionShop.Database.Table.Region>();

    public static synchronized com.geNAZt.RegionShop.Database.Table.Region isIn(Location playerLocation) {
        for(com.geNAZt.RegionShop.Database.Table.Region region : regionList) {
            if(region.getWorld().equals(playerLocation.getWorld().getName()) &&
               region.getMinX() <= playerLocation.getBlockX() &&
               region.getMinY() <= playerLocation.getBlockY() &&
               region.getMinZ() <= playerLocation.getBlockZ() &&
               region.getMaxX() >= playerLocation.getBlockX() &&
               region.getMaxY() >= playerLocation.getBlockY() &&
               region.getMaxZ() >= playerLocation.getBlockZ()) {
                return region;
            }
        }

        return null;
    }

    public static synchronized void warmCache() {
        regionList = Database.getServer().find(com.geNAZt.RegionShop.Database.Table.Region.class).findList();
    }
}
