package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Database.Model.Region;
import com.geNAZt.RegionShop.Events.WGChangeRegionEvent;
import com.geNAZt.RegionShop.Events.WGNewRegionEvent;

import com.geNAZt.RegionShop.Events.WGRemoveRegionEvent;
import com.geNAZt.RegionShop.Util.Logger;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 03.09.13
 */
public class WGChanges {
    public static void newRegion(WGNewRegionEvent event) {
        //Check if region is in DB
        if(!Region.isStored(event.getRegion(), event.getWorld())) {
            if(!Region.store(event.getRegion(), event.getWorld())) {
                Logger.error("Error storing a new Region");
            }
        }
    }

    public static void changeRegion(WGChangeRegionEvent event) {
        //Check if region is in DB
        if(Region.isStored(event.getNewRegion(), event.getWorld())) {
            if(!Region.update(event.getNewRegion(), event.getWorld())) {
                Logger.error("Error updating a region");
            }
        }
    }

    public static void removeRegion(WGRemoveRegionEvent event) {
        //Check if region is in DB
        if(Region.isStored(event.getRegion(), event.getWorld())) {
            if(!Region.remove(event.getRegion(), event.getWorld())) {
                Logger.error("Error deleting a region");
            }
        }
    }
}