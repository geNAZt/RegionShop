package net.cubespace.RegionShop.Util;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultBridge {
    public static Economy economy = null;

    static {
        RegisteredServiceProvider<Economy> economyProvider = Plugin.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        if (economy == null) {
            Logger.fatal("No Economy Plugin found.");
        }
    }
}
