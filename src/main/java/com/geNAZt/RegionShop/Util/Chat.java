package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.RegionShopPlugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 07.06.13
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
