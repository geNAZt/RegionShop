package net.cubespace.RegionShop.Events.ItemDB;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 29.10.13 15:53
 *
 * This event gets fired when the CSV Parser has been finished and you can add custom Blocks into the ItemDB
 */
public class PreEnableEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
