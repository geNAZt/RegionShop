package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.cache.Region;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class CacheWarming extends BukkitRunnable {
    @Override
    public void run() {
        Region.warmCache();

        Database.getServer().runCacheWarming();
    }
}
