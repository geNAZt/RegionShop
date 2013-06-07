package com.geNAZt.RegionShop.Event;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.PlayerStorage;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 05.06.13
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
public class LeaveEvent implements Listener {
    private RegionShopPlugin plugin;

    public LeaveEvent(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        if (PlayerStorage.getPlayer(e.getPlayer()) != null) {
           PlayerStorage.removerPlayer(e.getPlayer());
        }
    }
}
