package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Data.Storage.Drop;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Data.Storage.Search;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerQuit implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (InRegion.has(event.getPlayer())) InRegion.remove(event.getPlayer());

        if (Drop.has(event.getPlayer())) Drop.remove(event.getPlayer());

        if (Search.has(event.getPlayer())) Search.remove(event.getPlayer());
    }
}
