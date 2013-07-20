package com.geNAZt.RegionShop.Updater;

import com.geNAZt.RegionShop.RegionShopPlugin;

import java.io.*;
import java.util.Scanner;
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

    @SuppressWarnings("SameReturnValue")
    public static int getCurrentBuild() {
        return 4;
    }

    public static void update(int actualBuild) {
        for(Integer wishBuild = getCurrentBuild(); wishBuild > actualBuild; actualBuild++) {
            plugin.getLogger().info("Updater: Looking for Updates for Build " + actualBuild);

            CopyOnWriteArrayList<Update> updates;
            updates = loadFromJAR("com.geNAZt.RegionShop.Updater.Build" + actualBuild, Update.class, new Object[]{plugin});

            for(Object update : updates) {
                Update up = (Update) update;
                up.execute();
            }
        }

        plugin.getLogger().info("Updater: Updated to " + getCurrentBuild());

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

    public static void check() {
        //Check if Version has changed
        File versionFile = new File(plugin.getDataFolder().getAbsolutePath(), "version");
        if(versionFile.exists()) {
            try {
                StringBuilder fileContents = new StringBuilder((int)versionFile.length());
                Scanner scanner = new Scanner(versionFile);

                try {
                    while(scanner.hasNextLine()) {
                        fileContents.append(scanner.nextLine());
                    }

                    scanner.close();

                    String buildNumber = fileContents.toString();
                    Integer build;

                    try {
                        build = Integer.parseInt(buildNumber);

                        plugin.getLogger().info("Build Number: " + build);

                        if(build < Updater.getCurrentBuild()) {
                            //Needs updates
                            plugin.getLogger().info("===== Updating =====");
                            Updater.update(build);
                        }
                    } catch(NumberFormatException e) {
                        e.printStackTrace();
                    }

                } catch(NumberFormatException e) {
                    scanner.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                OutputStream oStream = new FileOutputStream(versionFile);
                oStream.write(String.valueOf(Updater.getCurrentBuild()).getBytes());
                oStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
