package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 08.06.13
 * Time: 21:01
 * To change this template use File | Settings | File Templates.
 */
public class ListStorage {
    private static class ShopListRegenerate extends BukkitRunnable {

        private final JavaPlugin plugin;

        public ShopListRegenerate(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        public void run() {
            if (plugin.getConfig().getBoolean("debug") == true) {
                plugin.getLogger().info("Generating new ShopList");
            }

            ListStorage.shopList = new HashMap<World, ArrayList<ProtectedRegion>>();
            Pattern r = Pattern.compile("(.*)regionshop(.*)");
            List <World> wrldList = plugin.getServer().getWorlds();

            for(World wrld : wrldList) {
                RegionManager rgMngr = WorldGuardBridge.getRegionManager(wrld);
                Map<String, ProtectedRegion> pRC = rgMngr.getRegions();
                ArrayList<ProtectedRegion> wrldRegions = new ArrayList<ProtectedRegion>();

                if(pRC.isEmpty()) {
                    continue;
                }

                for( Map.Entry<String, ProtectedRegion> regionEntry : pRC.entrySet()) {
                    Matcher m = r.matcher(regionEntry.getKey());

                    if (regionEntry.getValue().getFlag(DefaultFlag.TELE_LOC) != null && m.matches()) {
                        wrldRegions.add(regionEntry.getValue());
                    }
                }

                shopList.put(wrld, wrldRegions);

                plugin.getLogger().info("Found " + wrldRegions.size() + " shops in world " + wrld.getName());
            }
        }

    }

    public static HashMap<World, ArrayList<ProtectedRegion>> shopList = new HashMap<World, ArrayList<ProtectedRegion>>();
    private static BukkitTask task;
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;

        task = new ShopListRegenerate(pl).runTaskTimer(pl, 20, 300 * 20);
    }

    public static ArrayList<ProtectedRegion> getShopList(World wrld) {
        if (shopList.containsKey(wrld)) {
            return shopList.get(wrld);
        } else {
            return null;
        }
    }

    public static void reload() {
        task.cancel();
        task = new ShopListRegenerate(plugin).runTaskTimer(plugin, 20, 300 * 20);
    }
}
