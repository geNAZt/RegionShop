package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Database.Model.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 31.08.13
 */
public class CheckForNewPlayer implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //If a new User logs in save him
        if(!Player.isStored(event.getPlayer())) {
            Player.insertNewPlayer(event.getPlayer());
        }
    }
}
