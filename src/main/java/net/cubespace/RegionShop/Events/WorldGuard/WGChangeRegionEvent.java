package net.cubespace.RegionShop.Events.WorldGuard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class WGChangeRegionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private ProtectedRegion newRegion;
    private World world;

    public WGChangeRegionEvent(ProtectedRegion newRegion, World world) {
        this.newRegion = newRegion;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public ProtectedRegion getNewRegion() {
        return newRegion;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
