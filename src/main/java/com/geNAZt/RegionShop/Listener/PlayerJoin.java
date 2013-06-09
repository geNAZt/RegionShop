package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerJoin implements Listener {
    private final RegionShopPlugin plugin;

    public PlayerJoin(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        ArrayList<ProtectedRegion> regions = ListStorage.getShopList(e.getPlayer().getWorld());

        for (ProtectedRegion region : regions) {
            if(region.contains(e.getPlayer().getLocation().getBlockX(), e.getPlayer().getLocation().getBlockY(), e.getPlayer().getLocation().getBlockZ())) {
                PlayerStorage.setPlayer(e.getPlayer(), region.getId());

                String shopName = WorldGuardBridge.convertRegionToShopName(region, e.getPlayer().getWorld());
                if(shopName == null) {
                    shopName = region.getId();
                }

                e.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + shopName +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items");
                plugin.getLogger().info("[RegionShop] Player " + e.getPlayer().getDisplayName() + " entered WorldGuard Region " + region.getId());
                return;
            }
        }
    }
}
