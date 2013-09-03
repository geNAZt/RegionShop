package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.Resolver;
import com.geNAZt.RegionShop.Data.Storages.DropStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopEquip extends ShopCommand {
    private final RegionShopPlugin plugin;

    public ShopEquip(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 2;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop equip" + ChatColor.RESET + ": Toggle " + ChatColor.GRAY + "quick add"};
    }

    @Override
    public String getCommand() {
        return "equip";
    }

    @Override
    public String getPermissionNode() {
        return "rs.stock.equip";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            if (DropStorage.has(player)) {
                Region region = DropStorage.get(player);
                DropStorage.remove(player);

                player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Quick add mode for " + ChatColor.GREEN + region.getName() + ChatColor.GOLD + " disabled.");
                return;
            } else {
                HashSet<ProtectedRegion> foundRegions = WorldGuardBridge.searchRegionsByOwner(player.getName(), player.getWorld());

                if (foundRegions.size() == 0) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "No Shops found.");
                    return;
                }

                if (foundRegions.size() > 1) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Shop Selector" + ChatColor.YELLOW + " -- To select a shop: " + ChatColor.GOLD +"/shop equip " + ChatColor.RED + "shopname");
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
                    Region region1 = Resolver.resolve(region, player.getWorld());

                    plugin.getLogger().info("[RegionShop] Player "+ player.getDisplayName() +" has toggled "+ region.getId());

                    if(DropStorage.has(player)) {
                        DropStorage.remove(player);
                    }

                    DropStorage.set(player, region1);

                    player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Quick add mode for " + ChatColor.GREEN + region1.getName() + ChatColor.GOLD + " enabled. Drop items to add them to your shop stock");

                    return;
                }
            }
        }

        //Region warp
        ProtectedRegion region = WorldGuardBridge.convertShopNameToRegion(StringUtils.join(args, " "));

        if(region == null) {
            region = WorldGuardBridge.getRegionByString(StringUtils.join(args, " "), player.getWorld());
        }

        if (region != null) {
            Region region1 = Resolver.resolve(region, player.getWorld());

            if (!region.isOwner(player.getName())) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not an owner of this shop");
                return;
            }

            plugin.getLogger().info("[RegionShop] Player " + player.getName() + " has toggled " + region1.getName());

            if(DropStorage.has(player)) {
                DropStorage.remove(player);
            }

            DropStorage.set(player, region1);
            player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Shop " + ChatColor.GREEN + StringUtils.join(args, " ") + ChatColor.GOLD + " selected");

            return;
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This region could not be found");
    }
}