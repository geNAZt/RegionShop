package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Database.Database.Model.ShopShowcaseItem;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 30.08.13
 */
public class Showcase extends ShopCommand {
    private final RegionShopPlugin plugin;

    public Showcase(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 2;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop showcase " + ChatColor.RED + "itemID" + ChatColor.RESET + ": Setup a showcase for this ItemID"};
    }

    @Override
    public String getCommand() {
        return "showcase";
    }

    @Override
    public String getPermissionNode() {
        return "rs.showcase";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        Integer itemID = -1;

        try {
            itemID = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Only numbers as itemID value");
            return;
        }

        ShopShowcaseItem shopShowcaseItem = plugin.getDatabase().find(ShopShowcaseItem.class).
            where().
                eq("item", itemID).
            findUnique();
    }
}
