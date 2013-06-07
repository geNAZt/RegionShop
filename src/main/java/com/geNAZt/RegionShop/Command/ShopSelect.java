package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.PlayerStorage;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopSelect {
    private RegionShopPlugin plugin;

    public ShopSelect(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p, String playerOrRegion) {
        //Player warp
        if (plugin.getServer().getPlayer(playerOrRegion) != null) {
            HashSet<ProtectedRegion> foundRegions = WorldGuardBridge.searchRegionsByOwner(playerOrRegion, p);
            if (foundRegions.size() > 1) {
                p.sendMessage(Chat.getPrefix() + "This player has more than one Shop. Please select one out of the list /shop select <region>");
                for(ProtectedRegion region : foundRegions) {
                    p.sendMessage(region.getId());
                }

                return true;
            } else {
                for(ProtectedRegion region : foundRegions) {

                    plugin.getLogger().info("[RegionShop] Player "+ p.getDisplayName() +" has selected "+ region.getId());

                    if(PlayerStorage.getPlayer(p) != null) {
                        PlayerStorage.removerPlayer(p);
                    }

                    PlayerStorage.setPlayer(p, region.getId());
                    p.sendMessage(Chat.getPrefix() + "Shop " + playerOrRegion + " selected");

                    return true;
                }
            }
        }

        //Region warp
        if (WorldGuardBridge.isRegion(playerOrRegion, p)) {
            RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());
            ProtectedRegion region = rgMngr.getRegion(playerOrRegion);
            plugin.getLogger().info("[RegionShop] Player "+ p.getDisplayName() +" has selected "+  region.getId());

            if(PlayerStorage.getPlayer(p) != null) {
                PlayerStorage.removerPlayer(p);
            }

            PlayerStorage.setPlayer(p, region.getId());
            p.sendMessage(Chat.getPrefix() + "Shop " + playerOrRegion + " selected");

            return true;
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + "You havent given an Shop to select");
        return false;
    }
}
