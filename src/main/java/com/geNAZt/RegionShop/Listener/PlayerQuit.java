package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.DropStorage;
import com.geNAZt.RegionShop.Storages.PlayerStorage;

import com.geNAZt.RegionShop.Storages.SearchStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 05.06.13
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
public class PlayerQuit implements Listener {
    private RegionShopPlugin plugin;

    public PlayerQuit(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (PlayerStorage.getPlayer(e.getPlayer()) != null) {
           PlayerStorage.removerPlayer(e.getPlayer());
        }

        if (DropStorage.getPlayer(e.getPlayer()) != null) {
            DropStorage.removerPlayer(e.getPlayer());
        }

        SearchStorage.removeAllPlayer(e.getPlayer());
    }
}
