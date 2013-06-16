package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopWarp extends ShopCommand {
    private final Plugin plugin;

    public ShopWarp(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "warp";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPermissionNode() {
        return "rs.warp";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNumberOfArgs() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(Player player, String[] args) {
        //Player warp
        HashSet<ProtectedRegion> foundRegions;
        if (!(foundRegions = WorldGuardBridge.searchRegionsByOwner(args[0], player.getWorld())).isEmpty()) {
            if (foundRegions.size() > 1) {
                player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Shop Selector" + ChatColor.YELLOW + " -- To select a Shop: " + ChatColor.GOLD + "/shop warp <shopname>");
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

                plugin.getLogger().info("[RegionShop] Player "+ player.getDisplayName() +" was teleported to "+ region.getId());
                Vector tpVector = region.getFlag(DefaultFlag.TELE_LOC).getPosition();
                player.teleport(new Location(player.getWorld(), tpVector.getX(), tpVector.getY(), tpVector.getZ()));
                return;
            }
        }

        //Region warp
        ProtectedRegion region = WorldGuardBridge.convertShopNameToRegion(args[0]);

        if(region == null) {
            region = WorldGuardBridge.getRegionByString(args[0], player.getWorld());
        }

        if (region != null) {
            plugin.getLogger().info("[RegionShop] Player "+ player.getDisplayName() +" was teleported to "+  region.getId());
            Vector tpVector = region.getFlag(DefaultFlag.TELE_LOC).getPosition();
            player.teleport(new Location(player.getWorld(), tpVector.getX(), tpVector.getY(), tpVector.getZ()));
            return;
        }

        //Nothing of all
        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid player or shop name");
    }
}
