package com.geNAZt.RegionShop.Event;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.PlayerStorage;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 05.06.13
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
public class JoinEvent implements Listener {
    private RegionShopPlugin plugin;

    public JoinEvent(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        String storedRegion = null;
        Boolean found = false;

        if (PlayerStorage.getPlayer(e.getPlayer()) != null) {
            if (plugin.getConfig().getBoolean("debug")) {
                plugin.getLogger().info("[RegionShop] Player found in the storage");
            }

            storedRegion = PlayerStorage.getPlayer(e.getPlayer());
        }

        RegionManager rgMngr = WorldGuardBridge.getRegionManager(e.getPlayer().getWorld());
        ApplicableRegionSet rgSet = rgMngr.getApplicableRegions(e.getPlayer().getLocation());
        Pattern r = Pattern.compile("(.*)regionshop(.*)");

        for (ProtectedRegion region : rgSet) {
            if (storedRegion != null) {
                if (region.getId() == storedRegion) {
                    found = true;
                }
            } else {
                Matcher m = r.matcher(region.getId());
                if(m.matches()) {
                    PlayerStorage.setPlayer(e.getPlayer(), region.getId());

                    String shopName = WorldGuardBridge.convertRegionToShopName(region, e.getPlayer().getWorld());
                    if(shopName == null) {
                        shopName = region.getId();
                    }

                    e.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + shopName +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items");
                    plugin.getLogger().info("[RegionShop] Player " + e.getPlayer().getDisplayName() + " entered WG Region " + region.getId());
                } else {
                    if (plugin.getConfig().getBoolean("debug")) {
                        plugin.getLogger().info("[RegionShop] No ShopRegion " + region.getId());
                    }
                }
            }
        }
    }
}
