package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;

import java.io.File;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class Misc extends Config {
    public Misc(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "misc.yml");
        CONFIG_HEADER = new String[]{
            "This config holds some random Settings"
        };
    }

    public String chatPrefix = "[RS] ";
    public String regexPattern = "regionshop";
}
