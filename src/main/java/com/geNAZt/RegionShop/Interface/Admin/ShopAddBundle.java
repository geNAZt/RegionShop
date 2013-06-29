package com.geNAZt.RegionShop.Interface.Admin;

import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopBundle;
import com.geNAZt.RegionShop.Model.ShopRegion;
import com.geNAZt.RegionShop.Region.Region;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopAddBundle extends ShopCommand {
    private final Plugin plugin;

    public ShopAddBundle(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 4;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop admin addbundle " + ChatColor.RED + " name " + ChatColor.RESET + ": Adds or creates a bundle under the name."};
    }

    @Override
    public String getCommand() {
        return "addbundle";
    }

    @Override
    public String getPermissionNode() {
        return "rs.admin.addbundle";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        String name = StringUtils.join(args, " ");

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

        if(bundle != null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop is already inside a bundle: " + bundle.getName());
            return;
        }

        //Look if this bundle exists
        bundle = plugin.getDatabase().find(ShopBundle.class).
            where().
                eq("name", name).
            setMaxRows(1).
            findUnique();

        ShopBundle newBundle = new ShopBundle();
        newBundle.setName(name);
        newBundle.setWorld(player.getWorld().getName());
        newBundle.setRegion(region.getRegion().getId());

        //No bundle exists => This is the master
        if(bundle != null) {
            newBundle.setMaster(false);
        } else {
            newBundle.setMaster(true);
        }

        plugin.getDatabase().save(newBundle);

        //If an old ShopRegion entry is found delete it
        ShopRegion shopRegion = plugin.getDatabase().find(ShopRegion.class).
                where().
                    eq("region", region.getRegion().getId()).
                    eq("world", player.getWorld().getName()).
                findUnique();

        if(shopRegion != null) plugin.getDatabase().delete(shopRegion);

        //Create a new Bundle ShopRegion entry
        ShopRegion newShopRegion = new ShopRegion();
        newShopRegion.setBundle(true);
        newShopRegion.setName(name);
        newShopRegion.setWorld(player.getWorld().getName());
        newShopRegion.setRegion(region.getRegion().getId());

        plugin.getDatabase().save(newShopRegion);

        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop has been added to the Bundle: " + name);
    }
}
