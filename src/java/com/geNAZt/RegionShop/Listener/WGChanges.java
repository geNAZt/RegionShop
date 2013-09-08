package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Database.Model.Region;
import com.geNAZt.RegionShop.Events.WGChangeRegionEvent;
import com.geNAZt.RegionShop.Events.WGNewRegionEvent;

import com.geNAZt.RegionShop.Util.Logger;
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
        if(!Region.isStored(event.getRegion(), event.getWorld())) {
            if(!Region.store(event.getRegion(), event.getWorld())) {
                Logger.error("Error storing a new Region");
            }
        }
    }

    @EventHandler
    public void onChangeWGRegion(WGChangeRegionEvent event) {
        Logger.info("Got CHANGE event");

        //Check if region is in DB
        if(Region.isStored(event.getNewRegion(), event.getWorld())) {
            Region.update(event.getNewRegion(), event.getWorld());
        }
    }
}