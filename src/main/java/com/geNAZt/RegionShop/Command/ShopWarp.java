package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopWarp {
    private final RegionShopPlugin plugin;

    public ShopWarp(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute(Player p, String playerOrRegion) {
        //Player warp
        HashSet<ProtectedRegion> foundRegions;
        if ((foundRegions = WorldGuardBridge.searchRegionsByOwner(playerOrRegion, p.getWorld())) != null) {
            if (foundRegions.size() > 1) {
                p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Shop Selector" + ChatColor.YELLOW + " -- To select a Shop: " + ChatColor.GOLD +"/shop warp <shopname>");
                p.sendMessage(Chat.getPrefix() + " ");
                for(ProtectedRegion region : foundRegions) {
                    String name = WorldGuardBridge.convertRegionToShopName(region, p.getWorld());

                    if(name == null) {
                        name = region.getId();
                    }

                    p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + name);
                }

                return;
            } else {
                ProtectedRegion region = foundRegions.iterator().next();

                plugin.getLogger().info("[RegionShop] Player "+ p.getDisplayName() +" was teleported to "+ region.getId());
                Vector tpVector = region.getFlag(DefaultFlag.TELE_LOC).getPosition();
                p.teleport(new Location(p.getWorld(), tpVector.getX(), tpVector.getY(), tpVector.getZ()));
                return;
            }
        }

        //Region warp
        ProtectedRegion region = WorldGuardBridge.convertShopNameToRegion(playerOrRegion);

        if(region == null) {
            region = WorldGuardBridge.getRegionByString(playerOrRegion, p.getWorld());
        }

        if (region != null) {
            plugin.getLogger().info("[RegionShop] Player "+ p.getDisplayName() +" was teleported to "+  region.getId());
            Vector tpVector = region.getFlag(DefaultFlag.TELE_LOC).getPosition();
            p.teleport(new Location(p.getWorld(), tpVector.getX(), tpVector.getY(), tpVector.getZ()));
            return;
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid player or shop name");
    }
}
