package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.DropStorage;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;


/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopEquip {
    private RegionShopPlugin plugin;

    public ShopEquip(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p, String regionStr) {
        //Player warp
        if (regionStr == null) {
            if (DropStorage.getPlayer(p) != null) {
                regionStr = DropStorage.getPlayer(p);
                DropStorage.removerPlayer(p);

                RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());
                ProtectedRegion region = rgMngr.getRegion(regionStr);

                String shopName = WorldGuardBridge.convertRegionToShopName(region, p.getWorld());
                if(shopName == null) {
                    shopName = region.getId();
                }

                p.sendMessage(Chat.getPrefix() + ChatColor.GRAY + "Quick add " + ChatColor.GOLD + "mode for " + ChatColor.GREEN + shopName + ChatColor.GOLD + " disabled.");
                return true;
            } else {
                HashSet<ProtectedRegion> foundRegions = WorldGuardBridge.searchRegionsByOwner(p.getName(), p);

                if (foundRegions.size() == 0) {
                    p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "No Shops found.");
                    return true;
                }

                if (foundRegions.size() > 1) {
                    p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Shop Selector" + ChatColor.YELLOW + " -- To select a Shop: " + ChatColor.GOLD +"/shop equip <shopname>");
                    p.sendMessage(Chat.getPrefix() + " ");

                    for(ProtectedRegion region : foundRegions) {
                        String name = WorldGuardBridge.convertRegionToShopName(region, p.getWorld());

                        if(name == null) {
                            name = region.getId();
                        }

                        p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + name);
                    }

                    return true;
                } else {
                    for(ProtectedRegion region : foundRegions) {
                        plugin.getLogger().info("[RegionShop] Player "+ p.getDisplayName() +" has toggled "+ region.getId());

                        if(DropStorage.getPlayer(p) != null) {
                            DropStorage.removerPlayer(p);
                        }

                        DropStorage.setPlayer(p, region.getId());
                        String shopName = WorldGuardBridge.convertRegionToShopName(region, p.getWorld());
                        if(shopName == null) {
                            shopName = region.getId();
                        }

                        p.sendMessage(Chat.getPrefix() + ChatColor.GRAY + "Quick add " + ChatColor.GOLD + "mode for " + ChatColor.GREEN + shopName + ChatColor.GOLD + " enabled. Drop items to add them to your shop stock");

                        return true;
                    }
                }
            }
        }

        //Region warp
        ProtectedRegion region = WorldGuardBridge.convertShopNameToRegion(regionStr);

        if(region == null) {
            RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());
            region = rgMngr.getRegion(regionStr);
        }

        if (region != null) {
            if (!region.isOwner(p.getName())) {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You aren't a owner of this Shop");
                return true;
            }

            plugin.getLogger().info("[RegionShop] Player " + p.getName() + " has toggled " + region.getId());

            if(DropStorage.getPlayer(p) != null) {
                DropStorage.removerPlayer(p);
            }

            DropStorage.setPlayer(p, region.getId());
            p.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Shop " + ChatColor.GREEN + regionStr + ChatColor.GOLD + " selected");

            return true;
        }

        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "This Region couldn't be found");
        return false;
    }
}
