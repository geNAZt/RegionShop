package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Database.Model.ShopBundle;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopWarp extends ShopCommand {
    private class ValueComparator implements Comparator<ProtectedRegion> {

        Map<ProtectedRegion, Double> base;
        public ValueComparator(Map<ProtectedRegion, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(ProtectedRegion a, ProtectedRegion b) {
            if (base.get(a) <= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    private final RegionShopPlugin plugin;

    public ShopWarp(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop warp " + ChatColor.RED + "owner" + ChatColor.RESET + ": Warp to " + ChatColor.RED + "owner" + ChatColor.RESET + "'s shop",ChatColor.GOLD + "/shop warp " + ChatColor.RED + "shopname" + ChatColor.RESET + ": Warp to the shop called " + ChatColor.RED + "shopname"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCommand() {
        return "warp";
    }

    @Override
    public String getPermissionNode() {
        return "rs.warp";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        String shop = StringUtils.join(args, " ");

        //Bundle warp
        List<ShopBundle> bundledShops = plugin.getDatabase().find(ShopBundle.class).
                where().
                    eq("name", shop).
                findList();

        //Shop is a bundle
        if(bundledShops != null && !bundledShops.isEmpty()) {
            HashMap<ProtectedRegion,Double> map = new HashMap<ProtectedRegion,Double>();
            ValueComparator bvc =  new ValueComparator(map);
            TreeMap<ProtectedRegion,Double> sorted_map = new TreeMap<ProtectedRegion,Double>(bvc);

            for(ShopBundle bundleShop:bundledShops) {
                ProtectedRegion reg = WorldGuardBridge.getRegionByString(bundleShop.getRegion(), player.getWorld());
                Vector locVector = reg.getFlag(DefaultFlag.TELE_LOC).getPosition();

                map.put(reg, player.getLocation().distance(new Location(player.getWorld(), locVector.getX(), locVector.getY()+1, locVector.getZ())));
            }

            sorted_map.putAll(map);

            for(Map.Entry<ProtectedRegion, Double> entry : sorted_map.entrySet()) {
                plugin.getLogger().info(entry.getKey().getId() + ": " + entry.getValue());
            }

            ProtectedRegion regTP = sorted_map.firstKey();

            plugin.getLogger().info("[RegionShop] Player "+ player.getDisplayName() +" was teleported to "+ regTP.getId());
            Vector tpVector = regTP.getFlag(DefaultFlag.TELE_LOC).getPosition();
            player.teleport(new Location(player.getWorld(), tpVector.getX(), tpVector.getY()+1, tpVector.getZ()));
            return;
        }

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
                player.teleport(new Location(player.getWorld(), tpVector.getX(), tpVector.getY()+1, tpVector.getZ()));
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
            player.teleport(new Location(player.getWorld(), tpVector.getX(), tpVector.getY()+1, tpVector.getZ()));
            return;
        }

        //Nothing of all
        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid player or shop name");
    }
}
