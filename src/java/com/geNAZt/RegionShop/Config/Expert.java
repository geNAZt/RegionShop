package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.RegionShopPlugin;

import java.io.File;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class Expert extends Config {
    public Expert(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "expert.yml");
        CONFIG_HEADER = new String[]{
            "You only should alter this config file if you know what you do",
            "If you change something here the whole Server could be messed up after it",
            "You will find an exact documentation about all Config Settings at https://github.com/geNAZt/RegionShop/wiki"
        };
    }

    public Integer DB_maxConnections = 3;
    public Integer Timer_DetectWGChanges = 60*20;
    public Integer Tasks_AsyncDatabaseWriters = 1;
    public String Misc_regexPattern = "regionshop";
}
