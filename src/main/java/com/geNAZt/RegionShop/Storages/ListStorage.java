package com.geNAZt.RegionShop.Storages;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
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
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 08.06.13
 */
public class ListStorage {
    private static class ShopListRegenerate extends BukkitRunnable {

        private final JavaPlugin plugin;

        public ShopListRegenerate(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        public void run() {
            if (plugin.getConfig().getBoolean("debug")) {
                plugin.getLogger().info("Generating new ShopList");
            }

            ListStorage.shopList = new HashMap<World, ArrayList<ProtectedRegion>>();
            Pattern r = Pattern.compile("(.*)regionshop(.*)");
            List <World> wrldList = plugin.getServer().getWorlds();

            for(World wrld : wrldList) {
                Map<String, ProtectedRegion> pRC = WorldGuardBridge.getAllRegions(wrld);
                ArrayList<ProtectedRegion> wrldRegions = new ArrayList<ProtectedRegion>();

                if(pRC.isEmpty()) {
                    continue;
                }

                for( Map.Entry<String, ProtectedRegion> regionEntry : pRC.entrySet()) {
                    Matcher m = r.matcher(regionEntry.getKey());

                    if (m.matches()) {
                        if (regionEntry.getValue().getFlag(DefaultFlag.TELE_LOC) == null) {
                            BlockVector maxPoints = regionEntry.getValue().getMaximumPoint();
                            BlockVector minPoints = regionEntry.getValue().getMinimumPoint();

                            Vector loc = BlockVector.getMidpoint(maxPoints, minPoints);
                            loc = loc.setY(minPoints.getY());

                            regionEntry.getValue().setFlag(DefaultFlag.TELE_LOC, new Location(BukkitUtil.getLocalWorld(wrld), loc));
                        }

                        wrldRegions.add(regionEntry.getValue());
                    }
                }

                shopList.put(wrld, wrldRegions);

                plugin.getLogger().info("Found " + wrldRegions.size() + " shops in world " + wrld.getName());
            }
        }

    }

    private static HashMap<World, ArrayList<ProtectedRegion>> shopList = new HashMap<World, ArrayList<ProtectedRegion>>();
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

    public static ProtectedRegion getShopByRegion(String region, World wrld) {
        ArrayList<ProtectedRegion> regionsInWorld = getShopList(wrld);

        if (regionsInWorld == null) return null;

        for(ProtectedRegion regionObj : regionsInWorld) {
            if (regionObj.getId().equals(region)) {
                return regionObj;
            }
        }

        return null;
    }

    public static void reload() {
        task.cancel();
        task = new ShopListRegenerate(plugin).runTaskTimer(plugin, 20, 300 * 20);
    }
}
