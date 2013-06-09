package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Storages.DropStorage;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopEquip {
    private final RegionShopPlugin plugin;

    public ShopEquip(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void execute(Player p, String regionStr) {
        //Player warp
        if (regionStr == null) {
            if (DropStorage.getPlayer(p) != null) {
                regionStr = DropStorage.getPlayer(p);
                DropStorage.removerPlayer(p);

                ProtectedRegion region = WorldGuardBridge.getRegionByString(regionStr, p.getWorld());

                String shopName = WorldGuardBridge.convertRegionToShopName(region, p.getWorld());
                if(shopName == null) {
                    shopName = region.getId();
                }

                p.sendMessage(Chat.getPrefix() + ChatColor.GRAY + "Quick add " + ChatColor.GOLD + "mode for " + ChatColor.GREEN + shopName + ChatColor.GOLD + " disabled.");
                return;
            } else {
                HashSet<ProtectedRegion> foundRegions = WorldGuardBridge.searchRegionsByOwner(p.getName(), p.getWorld());

                if (foundRegions.size() == 0) {
                    p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "No Shops found.");
                    return;
                }

                if (foundRegions.size() > 1) {
                    p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Shop Selector" + ChatColor.YELLOW + " -- To select a shop: " + ChatColor.GOLD +"/shop equip <shopname>");
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

                    return;
                }
            }
        }

        //Region warp
        ProtectedRegion region = WorldGuardBridge.convertShopNameToRegion(regionStr);

        if(region == null) {
            region = WorldGuardBridge.getRegionByString(regionStr, p.getWorld());
        }

        if (region != null) {
            if (!region.isOwner(p.getName())) {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not an owner of this shop");
                return;
            }

            plugin.getLogger().info("[RegionShop] Player " + p.getName() + " has toggled " + region.getId());

            if(DropStorage.getPlayer(p) != null) {
                DropStorage.removerPlayer(p);
            }

            DropStorage.setPlayer(p, region.getId());
            p.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Shop " + ChatColor.GREEN + regionStr + ChatColor.GOLD + " selected");

            return;
        }

        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "This region could not be found");
    }
}
