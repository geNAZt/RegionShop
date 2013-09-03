package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;

import java.io.File;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class Language extends Config {
    public Language(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "language.yml");
        CONFIG_HEADER = new String[]{
            "This file holds all Language Strings. If you need to install another language",
            "You can find them at http://github.com/geNAZt/RegionShop"
        };
    }

    public String List_HelpText_InsideRegion = ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List items in the shop (inside a shopregion)";
    public String List_HelpText_OutSideRegion = ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List all shops (outside a shopregion)";
}
