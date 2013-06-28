package com.geNAZt.RegionShop.Interface.Admin;

import com.geNAZt.RegionShop.Events.RegionShopConfigReload;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Storages.ListStorage;
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

        //Look if this bundle exists

    }
}
