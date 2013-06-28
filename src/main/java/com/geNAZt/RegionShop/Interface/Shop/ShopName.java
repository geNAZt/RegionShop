package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopRegion;
import com.geNAZt.RegionShop.Region.Region;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.google.common.base.CharMatcher;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

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
    public int getHelpPage() {
        return 2;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop name " + ChatColor.RED + "shopname" + ChatColor.RESET + ": Rename your shop to " + ChatColor.RED + "shopname"};
    }

    @Override
    public String getCommand() {
        return "name";
    }

    @Override
    public String getPermissionNode() {
        return "rs.name";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        String name = StringUtils.join(args, " ");

        if(PlayerStorage.has(player)) {
            Region region = PlayerStorage.get(player);

            if (region.getRegion().isOwner(player.getName())) {
                if(region.isBundle()) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop is inside a bundle and can only be renamed via Admincommands");
                    return;
                }

                ArrayList<ProtectedRegion> regions = ListStorage.get(player.getWorld());

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
                                eq("name", region.getName()).
                                eq("world", player.getWorld().getName()).
                            endJunction().
                        findUnique();

                if (shpRegion != null) {
                    shpRegion.setName(name);
                    plugin.getDatabase().update(shpRegion);
                } else {
                    ShopRegion newShpRegion = new ShopRegion();
                    newShpRegion.setName(name);
                    newShpRegion.setRegion(region.getRegion().getId());
                    newShpRegion.setWorld(player.getWorld().getName());
                    newShpRegion.setBundle(false);
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
