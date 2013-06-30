package com.geNAZt.RegionShop.Updater;

import com.geNAZt.RegionShop.RegionShopPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        return 3;
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

        File versionFile = new File(plugin.getDataFolder().getAbsolutePath(), "version");

        if(versionFile.delete()) {
            try {
                OutputStream oStream = new FileOutputStream(versionFile);
                oStream.write(String.valueOf(Updater.getCurrentBuild()).getBytes());
                oStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().warning("Could not update Version file");
        }

    }
}
