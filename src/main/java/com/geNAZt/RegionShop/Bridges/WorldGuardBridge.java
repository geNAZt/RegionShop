package com.geNAZt.RegionShop.Bridges;

import com.geNAZt.RegionShop.Model.ShopRegion;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class WorldGuardBridge {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;
    }

    private static RegionManager getRegionManager(World world) {
        return WGBukkit.getRegionManager(world);
    }

    //Get all Regions that have the owner inside
    public static HashSet<ProtectedRegion> searchRegionsByOwner(String owner, World world) {
        HashSet<ProtectedRegion> proRegionCollection = new HashSet<ProtectedRegion>();
        ArrayList<ProtectedRegion> pRC = ListStorage.getShopList(world);

        if(pRC.isEmpty()) return null;

        for (ProtectedRegion region : pRC) {

            if (region.isOwner(owner)) {
                proRegionCollection.add(region);
            }
        }

        return proRegionCollection;
    }

    //Checks if string is a valid Region
    public static boolean isRegion(String region, World world) {
        RegionManager rgMngr = getRegionManager(world);

        return (rgMngr.getRegion(region) != null);
    }

    //Converts a ShopName into a region
    public static ProtectedRegion convertShopNameToRegion(String shopName) {
        ShopRegion shpRegion = plugin.getDatabase().find(ShopRegion.class).
                where().
                    eq("name", shopName).
                findUnique();

        if (shpRegion == null) return null;

        World wrld = plugin.getServer().getWorld(shpRegion.getWorld());
        RegionManager rgMngr = getRegionManager(wrld);

        return rgMngr.getRegion(shpRegion.getRegion());
    }

    //Converts a region (with world) into a ShopName
    public static String convertRegionToShopName(ProtectedRegion region, World world) {
        ShopRegion shpRegion = plugin.getDatabase().find(ShopRegion.class).
                where().
                    conjunction().
                        eq("region", region.getId()).
                        eq("world", world.getName()).
                    endJunction().
                findUnique();

        return (shpRegion != null) ? shpRegion.getName() : null;
    }

    //Gets the Region with the string
    public static ProtectedRegion getRegionByString(String region, World world) {
        return getRegionManager(world).getRegion(region);
    }

    //Get all Regions of a World
    public static Map<String, ProtectedRegion> getAllRegions(World world) {
        return getRegionManager(world).getRegions();
    }
}
