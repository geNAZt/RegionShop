package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Chest;
import com.geNAZt.RegionShop.Database.Table.Region;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
class MCStats {
    public static void init(RegionShopPlugin plugin) {
        try {
            Metrics metrics = new Metrics(plugin);
            Metrics.Graph graph = metrics.createGraph("# of Shops");

            graph.addPlotter(new Metrics.Plotter("Total Shops") {
                @Override
                public int getValue() {
                    Integer regionShopCount = Database.getServer().find(Region.class).findRowCount();
                    Integer chestShopCount = Database.getServer().find(Chest.class).findRowCount();

                    return regionShopCount + chestShopCount;
                }
            });

            graph.addPlotter(new Metrics.Plotter("Region Shops") {
                @Override
                public int getValue() {
                    return Database.getServer().find(Region.class).findRowCount();
                }
            });

            graph.addPlotter(new Metrics.Plotter("Chest Shops") {
                @Override
                public int getValue() {
                    return Database.getServer().find(Chest.class).findRowCount();
                }
            });

            metrics.start();
            RegionShopPlugin.getInstance().getLogger().info("Started profiling...");
        } catch (Exception e) {
            RegionShopPlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }
}
