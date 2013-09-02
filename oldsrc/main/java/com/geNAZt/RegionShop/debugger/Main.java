package com.geNAZt.RegionShop.debugger;

import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.debugger.Profiler.Profiler;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 22.07.13
 */
public class Main {
    public static void init(RegionShopPlugin plugin) {
        //Load config
        Config.init(plugin);

        if(Config.getConfig() != null &&
                (Config.getConfig().getBoolean("profiler.internal") || Config.getConfig().getBoolean("profiler.external"))) {
            Logger.debug("Debug started");
            Profiler.init(plugin);
        }
    }
}
