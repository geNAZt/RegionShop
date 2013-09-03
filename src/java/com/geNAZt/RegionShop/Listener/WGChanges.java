package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Events.WGNewRegionEvent;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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

    }

    private boolean isValidRegion(ProtectedRegion region) {
        return !region.getOwners().getPlayers().isEmpty();
    }
}