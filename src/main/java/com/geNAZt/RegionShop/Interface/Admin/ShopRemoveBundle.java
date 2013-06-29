package com.geNAZt.RegionShop.Interface.Admin;

import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopBundle;
import com.geNAZt.RegionShop.Model.ShopRegion;
import com.geNAZt.RegionShop.Region.Region;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopRemoveBundle extends ShopCommand {
    private final Plugin plugin;

    public ShopRemoveBundle(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 4;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop admin removebundle" + ChatColor.RESET + ": Removes the Shop out of the Bundle"};
    }

    @Override
    public String getCommand() {
        return "removebundle";
    }

    @Override
    public String getPermissionNode() {
        return "rs.admin.removebundle";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        //Look if player is inside a Shop
        if(!PlayerStorage.has(player)) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a Shop");
            return;
        }

        //Check if Shop already is inside a bundle
        Region region = PlayerStorage.get(player);
        ShopBundle bundle = plugin.getDatabase().find(ShopBundle.class).
                where().
                    eq("region", region.getRegion().getId()).
                findUnique();

        if(bundle == null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop is not inside a bundle");
            return;
        }

        //Bundle is master => move all itemStore contents into the new master
        if(bundle.isMaster()) {
            ShopBundle bundle1 = plugin.getDatabase().find(ShopBundle.class).
                    where().
                        eq("name", bundle.getName()).
                        ne("region", region.getRegion().getId()).
                    setMaxRows(1).
                    findUnique();

            bundle1.setMaster(true);

            plugin.getDatabase().update(bundle1);

            int row = plugin.getDatabase().
                    createSqlUpdate("UPDATE `ShopItems` SET `region` = :newregion WHERE `region` = :oldregion").
                    setParameter("newregion", bundle1.getRegion()).setParameter("oldregion", region.getRegion().getId()).execute();

            plugin.getDatabase().delete(bundle);

            ShopRegion shopRegion = plugin.getDatabase().find(ShopRegion.class).
                    where().
                        eq("region", region.getRegion().getId()).
                        eq("world", player.getWorld().getName()).
                    findUnique();

            plugin.getDatabase().delete(shopRegion);

            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop has been removed from the bundle");
        } else {
            plugin.getDatabase().delete(bundle);

            ShopRegion shopRegion = plugin.getDatabase().find(ShopRegion.class).
                    where().
                        eq("region", region.getRegion().getId()).
                        eq("world", player.getWorld().getName()).
                    findUnique();

            plugin.getDatabase().delete(shopRegion);

            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop has been removed from the bundle");
        }
    }
}
