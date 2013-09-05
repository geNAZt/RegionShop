package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Events.WGNewRegionEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 03.09.13
 */
public class WGChanges implements Listener {
    @EventHandler
    public void onNewWGRegion(WGNewRegionEvent event) {
        //Check if region is in DB
        Region region = Database.getServer().find(Region.class).
                where().
                    eq("region", event.getRegion().getId()).
                    eq("world", event.getWorld().getName()).
                findUnique();

        //If its not in the db check if valid and if it is insert it into the db
        if(region == null) {
            region = new Region();
            region.setName(event.getRegion().getId());
            region.setRegion(event.getRegion().getId());
        }
    }
}