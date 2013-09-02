package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Database.Database;
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
        Player player = Database.getServer().find(Player.class).where().eq("name", event.getPlayer().getName()).findUnique();

        if(player == null) {
            Player player1 = new Player();
            player1.setName(event.getPlayer().getName());

            Database.getQueue().add(player1);
        }
    }
}
