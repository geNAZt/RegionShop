package com.geNAZt.RegionShop.Updater;

import com.geNAZt.RegionShop.RegionShopPlugin;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.geNAZt.RegionShop.Util.Loader.loadFromJAR;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.06.13
 */
public class Updater {
    private static RegionShopPlugin plugin;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;
    }

    public static int getCurrentBuild() {
        return 2;
    }

    public static void update(int actualBuild) {
        for(Integer wishBuild = getCurrentBuild(); wishBuild > actualBuild; actualBuild++) {
            plugin.getLogger().info("Updater: Looking for Updates for Build " + actualBuild);

            CopyOnWriteArrayList<Update> updates;
            updates = loadFromJAR(plugin, "com.geNAZt.RegionShop.Updater.Build" + actualBuild, Update.class);

            for(Object update : updates) {
                Update up = (Update) update;
                up.execute();
            }
        }
    }
}
