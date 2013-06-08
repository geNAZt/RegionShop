package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopWarp {
    private RegionShopPlugin plugin;

    public ShopWarp(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean execute(Player p, String playerOrRegion) {
        //Player warp
        if (plugin.getServer().getPlayer(playerOrRegion) != null) {
            HashSet<ProtectedRegion> foundRegions = WorldGuardBridge.searchRegionsByOwner(playerOrRegion, p);
            if (foundRegions.size() > 1) {
                p.sendMessage(Chat.getPrefix() + "This player has more than one Shop. Please select one out of the list /shop warp <shopname>");
                for(ProtectedRegion region : foundRegions) {
                    String name = WorldGuardBridge.convertRegionToShopName(region, p.getWorld());

                    if(name == null) {
                        name = region.getId();
                    }

                    p.sendMessage(Chat.getPrefix() + name);
                }

                return true;
            } else {
                for(ProtectedRegion region : foundRegions) {
                    plugin.getLogger().info("[RegionShop] Player "+ p.getDisplayName() +" was teleported to "+ region.getId());
                    Vector tpVector = region.getFlag(DefaultFlag.TELE_LOC).getPosition();
                    p.teleport(new Location(p.getWorld(), tpVector.getX(), tpVector.getY(), tpVector.getZ()));
                    return true;
                }
            }
        }

        //Region warp
        ProtectedRegion region = WorldGuardBridge.convertShopNameToRegion(playerOrRegion);

        if(region == null) {
            RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());
            region = rgMngr.getRegion(playerOrRegion);
        }

        if (region != null) {
            plugin.getLogger().info("[RegionShop] Player "+ p.getDisplayName() +" was teleported to "+  region.getId());
            Vector tpVector = region.getFlag(DefaultFlag.TELE_LOC).getPosition();
            p.teleport(new Location(p.getWorld(), tpVector.getX(), tpVector.getY(), tpVector.getZ()));
            return true;
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + "Invalid Player or Shopname");
        return false;
    }
}
