package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Storages.DropStorage;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopEquip extends ShopCommand {
    private final Plugin plugin;

    public ShopEquip(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "equip";
    }

    @Override
    public String getPermissionNode() {
        return "rs.stock.equip";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNumberOfArgs() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args[0] == null) {
            if (DropStorage.getPlayer(player) != null) {
                args[0] = DropStorage.getPlayer(player);
                DropStorage.removerPlayer(player);

                ProtectedRegion region = WorldGuardBridge.getRegionByString(args[0], player.getWorld());

                String shopName = WorldGuardBridge.convertRegionToShopName(region, player.getWorld());
                if(shopName == null) {
                    shopName = region.getId();
                }

                player.sendMessage(Chat.getPrefix() + ChatColor.GRAY + "Quick add " + ChatColor.GOLD + "mode for " + ChatColor.GREEN + shopName + ChatColor.GOLD + " disabled.");
                return;
            } else {
                HashSet<ProtectedRegion> foundRegions = WorldGuardBridge.searchRegionsByOwner(player.getName(), player.getWorld());

                if (foundRegions.size() == 0) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "No Shops found.");
                    return;
                }

                if (foundRegions.size() > 1) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Shop Selector" + ChatColor.YELLOW + " -- To select a shop: " + ChatColor.GOLD +"/shop equip <shopname>");
                    player.sendMessage(Chat.getPrefix() + " ");

                    for(ProtectedRegion region : foundRegions) {
                        String name = WorldGuardBridge.convertRegionToShopName(region, player.getWorld());

                        if(name == null) {
                            name = region.getId();
                        }

                        player.sendMessage(Chat.getPrefix() + ChatColor.GREEN + name);
                    }

                    return;
                } else {
                    ProtectedRegion region = foundRegions.iterator().next();

                    plugin.getLogger().info("[RegionShop] Player "+ player.getDisplayName() +" has toggled "+ region.getId());

                    if(DropStorage.getPlayer(player) != null) {
                        DropStorage.removerPlayer(player);
                    }

                    DropStorage.setPlayer(player, region.getId());
                    String shopName = WorldGuardBridge.convertRegionToShopName(region, player.getWorld());
                    if(shopName == null) {
                        shopName = region.getId();
                    }

                    player.sendMessage(Chat.getPrefix() + ChatColor.GRAY + "Quick add " + ChatColor.GOLD + "mode for " + ChatColor.GREEN + shopName + ChatColor.GOLD + " enabled. Drop items to add them to your shop stock");

                    return;
                }
            }
        }

        //Region warp
        ProtectedRegion region = WorldGuardBridge.convertShopNameToRegion(args[0]);

        if(region == null) {
            region = WorldGuardBridge.getRegionByString(args[0], player.getWorld());
        }

        if (region != null) {
            if (!region.isOwner(player.getName())) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not an owner of this shop");
                return;
            }

            plugin.getLogger().info("[RegionShop] Player " + player.getName() + " has toggled " + region.getId());

            if(DropStorage.getPlayer(player) != null) {
                DropStorage.removerPlayer(player);
            }

            DropStorage.setPlayer(player, region.getId());
            player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Shop " + ChatColor.GREEN + args[0] + ChatColor.GOLD + " selected");

            return;
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This region could not be found");
    }
}
