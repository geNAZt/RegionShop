package com.geNAZt.RegionShop.Command.Shop;

import com.geNAZt.RegionShop.Model.ShopRegion;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.google.common.base.CharMatcher;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopName {
    private final RegionShopPlugin plugin;

    public ShopName(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute(Player p, String name) {
        if(PlayerStorage.getPlayer(p) != null) {
            String region = PlayerStorage.getPlayer(p);

            ProtectedRegion rgn = WorldGuardBridge.getRegionByString(region, p.getWorld());

            if (rgn.isOwner(p.getName())) {
                ArrayList<ProtectedRegion> regions = ListStorage.getShopList(p.getWorld());

                if(regions == null) return;

                for(ProtectedRegion wRegion : regions) {
                    String shopName = WorldGuardBridge.convertRegionToShopName(wRegion, p.getWorld());
                    if(shopName == null) {
                        shopName = wRegion.getId();
                    }

                    if (shopName.equalsIgnoreCase(name)) {
                        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "This name is already given to another shop");
                        return;
                    }
                }

                if(plugin.getConfig().getBoolean("only-ascii")) {
                    if (!CharMatcher.ASCII.matchesAllOf(name)) {
                        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You can only use ASCII characters for the name");
                        return;
                    }
                }

                if(name.length() > 30) {
                    p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Name length must be under 30 chars");
                    return;
                }

                ShopRegion shpRegion = plugin.getDatabase().find(ShopRegion.class).
                        where().
                            conjunction().
                                eq("region", rgn.getId()).
                                eq("world", p.getWorld().getName()).
                            endJunction().
                        findUnique();

                if (shpRegion != null) {
                    shpRegion.setName(name);
                    plugin.getDatabase().update(shpRegion);
                } else {
                    ShopRegion newShpRegion = new ShopRegion();
                    newShpRegion.setName(name);
                    newShpRegion.setRegion(rgn.getId());
                    newShpRegion.setWorld(p.getWorld().getName());
                    plugin.getDatabase().save(newShpRegion);
                }

                p.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "This shop has the name: " + ChatColor.GREEN + name);
                ListStorage.reload();
                return;
            } else {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not an owner in this shop");
                return;
            }
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a shop");
    }
}
