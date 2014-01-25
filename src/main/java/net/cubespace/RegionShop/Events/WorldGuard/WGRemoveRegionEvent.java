package net.cubespace.RegionShop.Events.WorldGuard;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class WGRemoveRegionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String region;
    private World world;

    public WGRemoveRegionEvent(String region, World world) {
        this.region = region;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public String getRegion() {
        return region;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
