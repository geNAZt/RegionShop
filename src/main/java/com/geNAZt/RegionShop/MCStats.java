package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Storages.ListStorage;
import org.mcstats.Metrics;

import java.io.IOException;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
class MCStats {
    public static void init(RegionShopPlugin plugin) {
        try {
            Metrics metrics = new Metrics(plugin);

            metrics.addCustomData(new Metrics.Plotter("Total Shops") {
                @Override
                public int getValue() {
                    return ListStorage.getTotalCount();
                }
            });

            metrics.start();
            plugin.getLogger().info("[Metrics] Started profiling...");
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }
}
