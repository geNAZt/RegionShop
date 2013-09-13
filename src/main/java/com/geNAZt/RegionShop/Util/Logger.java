package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.RegionShopPlugin;

import java.util.logging.Level;

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
        plugin.getLogger().info("[DEBUG] " + message);
    }

    public static synchronized void warn(String message) {
        plugin.getLogger().warning(message);
    }

    public static synchronized void info(String message) {
        plugin.getLogger().info(message);
    }

    public static synchronized void error(String message) {
        plugin.getLogger().log(Level.SEVERE, message);
    }

    public static synchronized void fatal(String message) {
        plugin.getLogger().log(Level.SEVERE, message);
        plugin.disable();
    }
}
