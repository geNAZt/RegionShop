package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopRegion;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.PlayerStorage;
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
public class ShopName {
    private RegionShopPlugin plugin;

    public ShopName(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean execute(Player p, String name) {
        if(PlayerStorage.getPlayer(p) != null) {
            String region = PlayerStorage.getPlayer(p);

            RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());
            ProtectedRegion rgn = rgMngr.getRegion(region);

            if (rgn.isOwner(p.getName())) {
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

                p.sendMessage(Chat.getPrefix() + "This Shop has the Name: " + name);
                return true;
            } else {
                p.sendMessage(Chat.getPrefix() + "You aren't owner in this Shop");
                return false;
            }
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + "You are not inside a Shop");
        return false;
    }
}
