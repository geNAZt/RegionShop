package com.geNAZt.RegionShop.Bukkit.Util;

import com.geNAZt.RegionShop.Bukkit.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Data.Storages.ListStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.Model.ShopBundle;
import com.geNAZt.RegionShop.Database.Model.ShopRegion;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 28.06.13
 */
public class Resolver {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;
    }

    public static Region resolve(ProtectedRegion region, World world) {
        return resolve(region.getId(), world);
    }

    public static Region resolve(Location location, World world) {
        //Get all Regions in this World
        ArrayList<ProtectedRegion> regions = ListStorage.get(world);

        //If no regions in this world, a player cant be in one
        if (regions == null) return null;

        //Check every region if the player is in it
        for (ProtectedRegion region : regions) {
            if(region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                String shopName = WorldGuardBridge.convertRegionToShopName(region, world);
                if(shopName == null) {
                    shopName = region.getId();
                }

                return resolve(shopName, world);
            }
        }

        //The player is in no shop
        return null;
    }

    public static Region resolve(String shopName, World world) {
        //Check if Region has a Meta DB Entry
        ShopRegion shopRegion = plugin.getDatabase().find(ShopRegion.class).
            where().
                eq("name", shopName).
            setMaxRows(1).
            findUnique();

        //Region has no Meta => Load Region based on string
        if (shopRegion == null) {
            return buildRegion(shopName, shopName, shopName, world, false);
        } else {
            //Check if Shop is part of a bundle
            if(shopRegion.isBundle()) {
                //Get the bundle master
                ShopBundle bundle = plugin.getDatabase().find(ShopBundle.class).
                    where().
                        conjunction().
                            eq("name", shopName).
                            eq("master", 1).
                        endJunction().
                    findUnique();

                //No bundle master ? => Broken Bundle
                if(bundle == null) {
                    Logger.warn("Bundle " + shopName + " has no master entry.");
                    return null;
                }

                return buildRegion(bundle.getName(), bundle.getRegion(), shopRegion.getRegion(), world, true);
            } else {
                return buildRegion(shopRegion.getName(), shopRegion.getRegion(), shopRegion.getRegion(), world, false);
            }
        }
    }

    private static Region buildRegion(String name, String itemstorage, String region, World world, boolean bundle) {
        //Check if Region is a shop
        if(!ListStorage.has(region, world)) return null;

        ProtectedRegion region2 = WorldGuardBridge.getRegionByString(region, world);
        if(region2 == null) return null;

        return buildRegion(name, itemstorage, region2, bundle);
    }

    private static Region buildRegion(String name, String itemstorage, ProtectedRegion region, boolean bundle) {
        Region region1 = new Region();
        region1.setName(name);
        region1.setRegion(region);
        region1.setItemStorage(itemstorage);
        region1.setBundle(bundle);

        return region1;
    }
}
