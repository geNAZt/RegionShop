package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Events.WGChangeRegionEvent;
import com.geNAZt.RegionShop.Events.WGNewRegionEvent;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class DetectWGChanges extends BukkitRunnable {
    //Save the State of the last run
    private HashMap<World, HashMap<String, ProtectedRegion>> lastCheckState = new HashMap<World, HashMap<String, ProtectedRegion>>();
    //Create the test Pattern
    private Pattern regex = Pattern.compile("(.*)" + ConfigManager.misc.regexPattern + "(.*)");
    //Hold the instance of the Plugin
    private RegionShopPlugin plugin;

    public DetectWGChanges(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        //Get all worlds
        List<World> worldList = Bukkit.getServer().getWorlds();
        for(World world : worldList) {
            //Create temporary null HashMap
            HashMap<String, ProtectedRegion> worldRegions;

            //Check if world is in lastCheckState list
            if(!lastCheckState.containsKey(world)) {
                worldRegions = new HashMap<String, ProtectedRegion>();
            } else {
                worldRegions = lastCheckState.get(world);
            }

            //Get all Regions currently registered in this World
            Map<String, ProtectedRegion> wgRegions = WGBukkit.getRegionManager(world).getRegions();

            //Check all wgRegions
            for(Map.Entry<String, ProtectedRegion> region : wgRegions.entrySet()) {
                Matcher matcher = regex.matcher(region.getKey());

                //Check if region is a ShopRegion
                if(matcher.matches()) {
                    //Is this region new ?
                    if(!worldRegions.containsKey(region.getKey())) {
                        //This is a new Region
                        worldRegions.put(region.getKey(), region.getValue());

                        //Generate a new Event
                        final WGNewRegionEvent wgNewRegionEvent = new WGNewRegionEvent(region.getValue(), world);

                        //Shedule the event
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getPluginManager().callEvent(wgNewRegionEvent);
                            }
                        });
                    } else {
                        //Has the region changed ?
                        ProtectedRegion protectedRegion = worldRegions.get(region.getKey());
                        if(!protectedRegion.equals(region.getValue())) {
                            //Generate a new Event
                            final WGChangeRegionEvent wgChangeRegionEvent = new WGChangeRegionEvent(protectedRegion, region.getValue(), world);

                            //Shedule the event
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Bukkit.getPluginManager().callEvent(wgChangeRegionEvent);
                                }
                            });

                            //Store the new Region
                            worldRegions.put(region.getKey(), region.getValue());
                        }
                    }
                }
            }

            //Save the HashMap
            lastCheckState.put(world, worldRegions);
        }
    }
}
