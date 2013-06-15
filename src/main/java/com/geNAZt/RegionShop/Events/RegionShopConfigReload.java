package com.geNAZt.RegionShop.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
public class RegionShopConfigReload extends Event {
    private static final HandlerList handlers = new HandlerList();

    public RegionShopConfigReload() {

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
