package net.cubespace.RegionShop.Util;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.cubespace.RegionShop.Bukkit.Plugin;

public class MailBridge {
    private static Essentials essentials = null;

    static {
        if (Plugin.getInstance().getServer().getPluginManager().isPluginEnabled("Essentials")) {
            essentials = (Essentials) Plugin.getInstance().getServer().getPluginManager().getPlugin("Essentials");
        }
    }

    public static boolean sendMail(String from, Object to, String message) {
        try {
            User usr = essentials.getUser(to);

            usr.addMail(from +": " + message);

            return true;
        } catch(NullPointerException e) {
            return false;
        }
    }
}
