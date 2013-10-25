package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.RegionShopPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 07.06.13
 */
public class VaultBridge {
    public static Economy economy = null;

    public static void init(RegionShopPlugin plugin) {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        if (economy == null) {
            Logger.fatal("No Economy Plugin found.");
        }
    }
}
