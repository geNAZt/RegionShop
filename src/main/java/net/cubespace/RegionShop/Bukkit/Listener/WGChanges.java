package net.cubespace.RegionShop.Bukkit.Listener;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Database.Repository.RegionRepository;
import net.cubespace.RegionShop.Events.WorldGuard.WGChangeRegionEvent;
import net.cubespace.RegionShop.Events.WorldGuard.WGNewRegionEvent;
import net.cubespace.RegionShop.Events.WorldGuard.WGRemoveRegionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class WGChanges implements Listener {
    @EventHandler
    public void onWGRegionCreate(final WGNewRegionEvent event) {
        Plugin.getInstance().getLogger().info("Got new Region");
        Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                RegionRepository.store(event.getRegion(), event.getWorld());
            }
        });
    }

    @EventHandler
    public void onWGRegionChange(final WGChangeRegionEvent event) {
        Plugin.getInstance().getLogger().info("Got changed Region");
        Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                RegionRepository.update(event.getNewRegion(), event.getWorld());
            }
        });
    }

    @EventHandler
    public void onWGRemoveRegion(final WGRemoveRegionEvent event) {
        Plugin.getInstance().getLogger().info("Got removed Region");
        Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                RegionRepository.remove(event.getRegion(), event.getWorld());
            }
        });
    }
}
