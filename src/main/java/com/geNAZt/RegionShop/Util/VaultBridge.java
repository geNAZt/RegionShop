package com.geNAZt.RegionShop.Util;

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

    public static boolean setupEconomy(RegionShopPlugin plugin)
    {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
