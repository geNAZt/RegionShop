package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerMove implements Listener {
    private final RegionShopPlugin plugin;

    public PlayerMove(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent e) {
        String storedRegion = null;
        Boolean found = false;

        if (PlayerStorage.getPlayer(e.getPlayer()) != null) {
            storedRegion = PlayerStorage.getPlayer(e.getPlayer());
        }

        ArrayList<ProtectedRegion> regions = ListStorage.getShopList(e.getPlayer().getWorld());

        if (regions == null) return;

        for (ProtectedRegion region : regions) {
            if(region.contains(e.getPlayer().getLocation().getBlockX(), e.getPlayer().getLocation().getBlockY(), e.getPlayer().getLocation().getBlockZ())) {
                if (storedRegion != null) {
                    if (region.getId().equals(storedRegion)) {
                        found = true;
                        break;
                    }
                } else {
                    PlayerStorage.setPlayer(e.getPlayer(), region.getId());

                    String shopName = WorldGuardBridge.convertRegionToShopName(region, e.getPlayer().getWorld());
                    if(shopName == null) {
                        shopName = region.getId();
                    }

                    e.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + shopName +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items");
                    plugin.getLogger().info("Player " + e.getPlayer().getDisplayName() + " entered WG Region " + region.getId());
                }
            }
        }

        if (storedRegion != null && !found) {
            ProtectedRegion region = WorldGuardBridge.getRegionByString(storedRegion, e.getPlayer().getWorld());
            String shopName = WorldGuardBridge.convertRegionToShopName(region, e.getPlayer().getWorld());

            if(shopName == null) {
                shopName = region.getId();
            }

            e.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have left " + ChatColor.DARK_GREEN + shopName +  ChatColor.GOLD + ". Bye!");
            plugin.getLogger().info("Player "+ e.getPlayer().getDisplayName() + " left WG Region " + storedRegion);
            PlayerStorage.removerPlayer(e.getPlayer());
        }
    }
}
