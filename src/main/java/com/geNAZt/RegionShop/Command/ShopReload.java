package com.geNAZt.RegionShop.Command;


import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopReload {
    private final RegionShopPlugin plugin;

    public ShopReload(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void execute(Player p) {
        ListStorage.reload();
        plugin.reloadConfig();

        p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "Reloaded " + ChatColor.GOLD + "Config/ShopList");
    }
}
