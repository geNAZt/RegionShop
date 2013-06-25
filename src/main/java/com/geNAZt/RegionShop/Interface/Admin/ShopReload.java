package com.geNAZt.RegionShop.Interface.Admin;

import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Events.RegionShopConfigReload;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopReload extends ShopCommand {
    private final Plugin plugin;

    public ShopReload(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 4;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop admin reload " + ChatColor.RESET + ": Reload the RegionShop config and ListStorage"};
    }

    @Override
    public String getCommand() {
        return "reload";
    }

    @Override
    public String getPermissionNode() {
        return "rs.admin.reload";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        ListStorage.reload();
        plugin.reloadConfig();

        plugin.getServer().getPluginManager().callEvent(new RegionShopConfigReload());

        player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "Reloaded " + ChatColor.GOLD + "Config/ShopList");
    }
}