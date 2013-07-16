package com.geNAZt.RegionShop.Bukkit.Util;

import com.geNAZt.RegionShop.RegionShopPlugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 14.07.13
 */
public class Logger {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin plugin) {
        Logger.plugin = plugin;
    }

    public static synchronized void debug(String message) {
        if (plugin.getConfig().getBoolean("debug", true)) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    public static synchronized void warn(String message) {
        plugin.getLogger().warning(message);
    }

    public static synchronized void info(String message) {
        plugin.getLogger().info(message);
    }
}
