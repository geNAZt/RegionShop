package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.09.2013
 */
public class PlayerMove implements Listener {
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        //Check if Event is in an enabled world
        if (!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
            return;
        }

        //Since we only do DB operations we can do it async
        final Region foundRegion = com.geNAZt.RegionShop.Database.Model.Region.isIn(event.getPlayer());

        if (InRegion.has(event.getPlayer())) {
            final Region stored = InRegion.get(event.getPlayer());
            if (foundRegion == null || !stored.getRegion().equals(foundRegion.getRegion())) {
                //Put something out => Needs to be synced
                event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Shop_Leave.replace("%name", stored.getName()));
                RegionShopPlugin.getInstance().getLogger().info("Player " + event.getPlayer().getDisplayName() + " left WG Region " + stored.getName());

                InRegion.remove(event.getPlayer());

                //Has User stepped from one region into another ?
                if (foundRegion != null) {
                    //Put something out => Needs to be synced
                    event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Shop_Enter.replace("%name", foundRegion.getName()));
                    RegionShopPlugin.getInstance().getLogger().info("Player " + event.getPlayer().getDisplayName() + " entered WG Region " + foundRegion.getName());
                    InRegion.put(event.getPlayer(), foundRegion);
                }
            }
        } else {
            //Has User stepped into a new Region ?
            if (foundRegion != null) {
                event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Shop_Enter.replace("%name", foundRegion.getName()));
                RegionShopPlugin.getInstance().getLogger().info("Player " + event.getPlayer().getDisplayName() + " entered WG Region " + foundRegion.getName());

                InRegion.put(event.getPlayer(), foundRegion);
            }
        }
    }
}
