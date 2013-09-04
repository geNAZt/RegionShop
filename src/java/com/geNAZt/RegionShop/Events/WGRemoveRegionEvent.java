package com.geNAZt.RegionShop.Events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class WGRemoveRegionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private ProtectedRegion region;
    private World world;

    public WGRemoveRegionEvent(ProtectedRegion region, World world) {
        this.region = region;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
