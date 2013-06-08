package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.RegionShopPlugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 08.06.13
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
public class EssentialBridge {
    private static RegionShopPlugin plugin;
    private static Essentials essentials = null;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;

        if (plugin.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
        }
    }

    public  static boolean hasEssentials() {
        return (essentials != null);
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    public static boolean sendMail(String from, Object to, String message) {
        try {
            Essentials ess = getEssentials();
            User usr = ess.getUser(to);

            usr.addMail(from +": " + message);

            return true;
        } catch(NullPointerException e) {
            return false;
        }
    }
}
