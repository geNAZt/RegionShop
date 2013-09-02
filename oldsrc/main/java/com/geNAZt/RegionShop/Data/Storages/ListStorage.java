package com.geNAZt.RegionShop.Data.Storages;

import com.geNAZt.RegionShop.Bukkit.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Data.Tasks.ShopListRegenerate;
import com.geNAZt.RegionShop.Database.Database.Model.ShopRegion;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 08.06.13
 */
public class ListStorage {
    private static ConcurrentHashMap<World, ArrayList<ProtectedRegion>> shopList = new ConcurrentHashMap<World, ArrayList<ProtectedRegion>>();
    private static BukkitTask task;
    private static RegionShopPlugin plugin;

    //Start the Storage
    public static void init(RegionShopPlugin pl) {
        plugin = pl;

        //Load all Shops from Database
        List<ShopRegion> shopRegions = plugin.getDatabase().find(ShopRegion.class).findList();
        for(ShopRegion shopRegion : shopRegions) {
            World world = plugin.getServer().getWorld(shopRegion.getWorld());

            //The world where the Shop is in does not exist
            if(world == null) {
                plugin.getLogger().warning("Found a Shop which has an not existing World. Deleting the shop...");
            }
        }

        task = new ShopListRegenerate(pl).runTaskTimerAsynchronously(pl, 20, 60 * 20);
    }

    //Get a unique list of all shop in a world
    public static HashMap<String, ProtectedRegion> getUnique(World world) {
        ArrayList<ProtectedRegion> list = get(world);

        if(list == null) return null;

        HashMap<String, ProtectedRegion> resultMap = new HashMap<String, ProtectedRegion>();

        for(ProtectedRegion region : list) {
            String shopName = WorldGuardBridge.convertRegionToShopName(region, world);
            shopName = ((shopName != null) ? shopName : region.getId());

            if(!resultMap.containsKey(shopName)) {
                resultMap.put(shopName, region);
            }
        }

        return resultMap;
    }

    //Get all Shops in the World
    public static ArrayList<ProtectedRegion> get(World wrld) {
        if (shopList.containsKey(wrld)) {
            return shopList.get(wrld);
        } else {
            return null;
        }
    }

    //Get the region in world if it is a Shop
    public static ProtectedRegion get(String region, World wrld) {
        ArrayList<ProtectedRegion> regionsInWorld = get(wrld);

        if (regionsInWorld == null) return null;

        for(ProtectedRegion regionObj : regionsInWorld) {
            if (regionObj.getId().equals(region)) {
                return regionObj;
            }
        }

        return null;
    }

    //Check if region in world is a Shop
    public static boolean has(String region, World world) {
        ArrayList<ProtectedRegion> regions = get(world);

        for(ProtectedRegion region1:regions) {
            if(region1.getId().equals(region)) {
                return true;
            }
        }

        return false;
    }

    //Replace all Shops with the new List (from the RegenTask)
    public static void replace(ConcurrentHashMap<World, ArrayList<ProtectedRegion>> newShopList) {
        shopList = newShopList;
    }

    //Force reload for the ShopList
    public static void reload() {
        task.cancel();
        task = new ShopListRegenerate(plugin).runTaskTimer(plugin, 0, 60 * 20);
    }

    //Get the count of all loaded Shops
    public static int getTotalCount() {
        int total = 0;

        for(Map.Entry<World, ArrayList<ProtectedRegion>> entry : shopList.entrySet()) {
            total += entry.getValue().size();
        }

        return total;
    }
}
