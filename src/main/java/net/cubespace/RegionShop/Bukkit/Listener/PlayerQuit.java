package net.cubespace.RegionShop.Bukkit.Listener;

import net.cubespace.RegionShop.Data.Storage.Drop;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Data.Storage.Search;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (InRegion.has(event.getPlayer())) InRegion.remove(event.getPlayer());

        if (Drop.has(event.getPlayer())) Drop.remove(event.getPlayer());

        if (Search.has(event.getPlayer())) Search.remove(event.getPlayer());
    }
}
