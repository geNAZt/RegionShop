package com.geNAZt.RegionShop.Command.Shop;


import com.geNAZt.RegionShop.Events.RegionShopConfigReload;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Util.Chat;
import org.bukkit.Bukkit;
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

        plugin.getServer().getPluginManager().callEvent(new RegionShopConfigReload());

        p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "Reloaded " + ChatColor.GOLD + "Config/ShopList");
    }
}
