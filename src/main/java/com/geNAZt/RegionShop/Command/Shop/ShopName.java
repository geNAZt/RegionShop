package com.geNAZt.RegionShop.Command.Shop;

import com.geNAZt.RegionShop.Command.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopRegion;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;

import com.google.common.base.CharMatcher;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopName extends ShopCommand {
    private final Plugin plugin;

    public ShopName(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "name";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPermissionNode() {
        return "rs.name";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNumberOfArgs() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(Player player, String[] args) {
        String name = StringUtils.join(args, " ");

        if(PlayerStorage.getPlayer(player) != null) {
            String region = PlayerStorage.getPlayer(player);

            ProtectedRegion rgn = WorldGuardBridge.getRegionByString(region, player.getWorld());

            if (rgn.isOwner(player.getName())) {
                ArrayList<ProtectedRegion> regions = ListStorage.getShopList(player.getWorld());

                if(regions == null) return;

                for(ProtectedRegion wRegion : regions) {
                    String shopName = WorldGuardBridge.convertRegionToShopName(wRegion, player.getWorld());
                    if(shopName == null) {
                        shopName = wRegion.getId();
                    }

                    if (shopName.equalsIgnoreCase(name)) {
                        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This name is already given to another shop");
                        return;
                    }
                }

                if(plugin.getConfig().getBoolean("only-ascii")) {
                    if (!CharMatcher.ASCII.matchesAllOf(name)) {
                        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You can only use ASCII characters for the name");
                        return;
                    }
                }

                if(name.length() > 30) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Name length must be under 30 chars");
                    return;
                }

                ShopRegion shpRegion = plugin.getDatabase().find(ShopRegion.class).
                        where().
                        conjunction().
                        eq("region", rgn.getId()).
                        eq("world", player.getWorld().getName()).
                        endJunction().
                        findUnique();

                if (shpRegion != null) {
                    shpRegion.setName(name);
                    plugin.getDatabase().update(shpRegion);
                } else {
                    ShopRegion newShpRegion = new ShopRegion();
                    newShpRegion.setName(name);
                    newShpRegion.setRegion(rgn.getId());
                    newShpRegion.setWorld(player.getWorld().getName());
                    plugin.getDatabase().save(newShpRegion);
                }

                player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "This shop has the name: " + ChatColor.GREEN + name);
                ListStorage.reload();
                return;
            } else {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not an owner in this shop");
                return;
            }
        }

        //Nothing of all
        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a shop");
    }
}
