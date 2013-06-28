package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Storages.DropStorage;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Storages.SearchStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (PlayerStorage.has(e.getPlayer())) {
            PlayerStorage.remove(e.getPlayer());
        }

        if (DropStorage.has(e.getPlayer())) {
            DropStorage.remove(e.getPlayer());
        }

        SearchStorage.removeAllPlayer(e.getPlayer());
    }
}
