package net.cubespace.RegionShop.Events.Plugin;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 12:21
 *
 * This event gets fired if the RegionShop has been loaded from Bukkit
 */
public class RegionShopEnabledEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String version;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the current Version of RegionShop. For example "3.1.2"
     * @return
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
