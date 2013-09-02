package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.RegionShopPlugin;

import java.io.File;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class TimingConfig extends Config {
    public TimingConfig(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "timings.yml");
        CONFIG_HEADER = new String[]{
            "This configuration holds ALL Timer intervals from the RegionShop",
            "Please only alter this Values if you really need to",
            "All Timings are given in ticks (20 Ticks => 1 Second)",
            "DetectWGChanges = Interval for checking for new Regions, deleted Regions or redefined regions",
            "AsyncDatabaseWriters = Amount of threads which handle all writing into the Database"
        };
    }

    public Integer DetectWGChanges = 60*20;
    public Integer AsyncDatabaseWriters = 1;
}
