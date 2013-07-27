package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Bukkit.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.Data.Storages.ListStorage;
import com.geNAZt.debugger.Profiler.Profiler;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 28.06.13
 */
public class ShopListRegenerate extends BukkitRunnable {
    private final JavaPlugin plugin;

    public ShopListRegenerate(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        Profiler.start("ShopListRegenerate");

        Logger.debug("Generating new ShopList");

        ConcurrentHashMap<World, ArrayList<ProtectedRegion>> shopList = new ConcurrentHashMap<World, ArrayList<ProtectedRegion>>();
        Pattern r = Pattern.compile("(.*)regionshop(.*)");
        List<World> wrldList = plugin.getServer().getWorlds();

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

        ListStorage.replace(shopList);

        Profiler.end("ShopListRegenerate");
    }
}
