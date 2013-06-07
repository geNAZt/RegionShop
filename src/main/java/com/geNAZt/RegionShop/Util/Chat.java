package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.RegionShopPlugin;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 07.06.13
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
public class Chat {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;
    }

    public static String getPrefix() {
        return plugin.getConfig().getString("chat.prefix");
    }
}
