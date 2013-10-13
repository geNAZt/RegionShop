package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Data.Storage.Update;
import com.geNAZt.RegionShop.Database.Model.Player;
import com.geNAZt.RegionShop.Database.Model.Region;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.Bukkit;
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
    public void onPlayerJoin(final PlayerJoinEvent event) {
        //Check if player can be shown update informations
        if(event.getPlayer().hasPermission("rs.update") && Update.isUpdate()) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Updater_NewUpdate.replace("%version", Update.getVersion()).replace("%link", Update.getLink()));
        }

        //Since we only do DB operations we can do it async
        Bukkit.getServer().getScheduler().runTaskAsynchronously(RegionShopPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                //If a new User logs in save him
                if(!Player.isStored(event.getPlayer())) {
                    Player.insertNewPlayer(event.getPlayer());
                }

                //Check if user is inside a Region
                final com.geNAZt.RegionShop.Database.Table.Region region = Region.isIn(event.getPlayer());

                if(region != null) {
                    //We want to send a message => Needs to be synced in the main thread
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RegionShopPlugin.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Shop_Enter.replace("%name", region.getName()));
                            RegionShopPlugin.getInstance().getLogger().info("Player " + event.getPlayer().getDisplayName() + " entered WG Region " + region.getName());
                        }
                    });

                    InRegion.put(event.getPlayer(), region);
                }
            }
        });
    }
}
