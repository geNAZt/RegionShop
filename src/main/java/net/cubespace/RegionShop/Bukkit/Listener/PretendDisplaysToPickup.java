package net.cubespace.RegionShop.Bukkit.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PretendDisplaysToPickup implements Listener {
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if(event.getItem().getItemStack().hasItemMeta() && event.getItem().getItemStack().getItemMeta().getDisplayName().contains("RegionShop")) {
            event.setCancelled(true);
        }
    }
}
