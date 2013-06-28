package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.RegionShopPlugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 28.06.13
 */
public class AdminTeller {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;
    }

    public static void tell(String message) {
        if(plugin.getConfig().getBoolean("adminteller.console", true)) {
            sendToConsole(message);
        }
    }

    private static void sendToConsole(String message) {
        plugin.getLogger().warning("AdminTeller: " + message);
    }
}
