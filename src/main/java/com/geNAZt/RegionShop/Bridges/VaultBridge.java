package com.geNAZt.RegionShop.Bridges;

import com.geNAZt.RegionShop.RegionShopPlugin;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 07.06.13
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class VaultBridge {
    public static Economy economy = null;

    public static void init(RegionShopPlugin plugin) {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        if (economy == null) {
            plugin.getLogger().warning("No Economy Plugin found.");
            plugin.disable();
        }
    }
}
