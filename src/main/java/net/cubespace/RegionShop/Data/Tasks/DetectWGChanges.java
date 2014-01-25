package net.cubespace.RegionShop.Data.Tasks;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Events.WorldGuard.WGChangeRegionEvent;
import net.cubespace.RegionShop.Events.WorldGuard.WGNewRegionEvent;
import net.cubespace.RegionShop.Events.WorldGuard.WGRemoveRegionEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DetectWGChanges extends BukkitRunnable {
    //Create the test Pattern
    private Pattern regex = Pattern.compile("(.*)" + ConfigManager.main.Expert_WGRegex + "(.*)");

    @Override
    public void run() {
        //Get all worlds
        List<World> worldList = Bukkit.getServer().getWorlds();
        for(World world : worldList) {
            //Check if World is enabled
            if(!ConfigManager.main.World_enabledWorlds.contains(world.getName())) {
                continue;
            }

            //Get all Regions currently registered in this World
            Map<String, ProtectedRegion> wgRegions = WGBukkit.getRegionManager(world).getRegions();

            //Check all wgRegions
            for(Map.Entry<String, ProtectedRegion> region : wgRegions.entrySet()) {
                Matcher matcher = regex.matcher(region.getKey());

                //Check if region is a ShopRegion
                if(matcher.matches()) {
                    //Get the Region out of the Database
                    Region dbRegion;
                    try {
                        dbRegion = (Region) Database.getDAO(Region.class).
                                queryBuilder().
                                    where().
                                        eq("region", region.getKey()).
                                        and().
                                        eq("world", world.getName()).
                                queryForFirst();
                    } catch (SQLException e) {
                        Plugin.getInstance().getLogger().warning("Could not get Region due to a SQL Exceptio");
                        e.printStackTrace();

                        continue;
                    }

                    //Is this region new ?
                    if(dbRegion == null) {
                        //Region is not in the list but maybe it is invalid ?
                        if(region.getValue().getOwners().getPlayers().isEmpty()) {
                            continue;
                        }

                        //Generate a new Event
                        final WGNewRegionEvent wgNewRegionEvent = new WGNewRegionEvent(region.getValue(), world);
                        Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                Plugin.getInstance().getServer().getPluginManager().callEvent(wgNewRegionEvent);
                            }
                        });
                    } else {
                        //Check if region changed
                        if( dbRegion.getOwners().size() != region.getValue().getOwners().getPlayers().size() || //Size of owners has changed
                            dbRegion.getMembers().size() != region.getValue().getMembers().getPlayers().size() // Size of members has changed
                            ) {
                            //Generate a new Event
                            final WGChangeRegionEvent wgChangeRegionEvent = new WGChangeRegionEvent(region.getValue(), world);
                            Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    Plugin.getInstance().getServer().getPluginManager().callEvent(wgChangeRegionEvent);
                                }
                            });
                        }
                    }
                }
            }

            //Check if Regions where removed
            try {
                for(Region region : (List<Region>) Database.getDAO(Region.class).queryForEq("world", world.getName())) {
                    //This region is deleted ?
                    if(!wgRegions.containsKey(region.getRegion())) {
                        //Generate a new Event
                        final WGRemoveRegionEvent wgRemoveRegionEvent = new WGRemoveRegionEvent(region.getRegion(), world);
                        Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                Plugin.getInstance().getServer().getPluginManager().callEvent(wgRemoveRegionEvent);
                            }
                        });
                    }
                }
            } catch (SQLException e) {
                Plugin.getInstance().getLogger().warning("Could not get Regions due to a SQL Exceptio");
                e.printStackTrace();
            }
        }
    }
}