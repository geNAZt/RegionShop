package com.geNAZt.RegionShop.Bridges;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.geNAZt.RegionShop.RegionShopPlugin;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 08.06.13
 */
public class EssentialBridge {
    private static Essentials essentials = null;

    public static void init(RegionShopPlugin plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
        }
    }

    private static Essentials getEssentials() {
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
