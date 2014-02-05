package net.cubespace.RegionShop.Util;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultBridge {
    private final static Economy economy;

    static {
        RegisteredServiceProvider<Economy> economyProvider = Plugin.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        } else {
            economy = null;
            Logger.fatal("No Economy Plugin found.");
        }
    }

    public static boolean has(String account, double money) {
        synchronized (economy) {
            return economy.has(account, money);
        }
    }

    public static EconomyResponse withdrawPlayer(String account, double money) {
        synchronized (economy) {
            return economy.withdrawPlayer(account, money);
        }
    }

    public static EconomyResponse depositPlayer(String account, double money) {
        synchronized (economy) {
            return economy.depositPlayer(account, money);
        }
    }
}
