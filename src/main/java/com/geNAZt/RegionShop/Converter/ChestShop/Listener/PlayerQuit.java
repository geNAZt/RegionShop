package com.geNAZt.RegionShop.Converter.ChestShop.Listener;

import com.geNAZt.RegionShop.Converter.ChestShop.ConvertStorage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(ConvertStorage.hasPlayer(e.getPlayer())) {
            ConvertStorage.removerPlayer(e.getPlayer());
        }
    }
}
