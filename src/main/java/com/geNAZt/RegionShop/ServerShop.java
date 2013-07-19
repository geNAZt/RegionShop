package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Data.Tasks.ConfigReaderTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class ServerShop {
    public static BukkitTask priceRecalculateTask = null;
    public static BukkitTask itemAverageTask = null;
    private static RegionShopPlugin plugin;
    private static CopyOnWriteArrayList<FileConfiguration> configs = new CopyOnWriteArrayList<FileConfiguration>();
    public static boolean isEnabled = false;

    public static void init(RegionShopPlugin plugin) {
        isEnabled = true;
        ServerShop.plugin = plugin;

        loadAllServerShopConfigs();
    }

    private static void unload() {
        isEnabled = false;

        configs = new CopyOnWriteArrayList<FileConfiguration>();

        if(priceRecalculateTask != null) priceRecalculateTask.cancel();
    }

    private static void loadAllServerShopConfigs() {
        File file = new File(plugin.getDataFolder(), "servershop");
        if(!file.exists()) {
            plugin.getLogger().warning("ServerShops enabled but no servershop config Directory found - Unloading ServerShop");
            ServerShop.unload();
            return;
        }

        File[] files = file.listFiles();
        if (files != null) {
            for (File child : files) {
                plugin.getLogger().info("Loading ServerShop config: " + child.getAbsolutePath());

                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(child);
                if(fileConfiguration != null) configs.add(fileConfiguration);
            }
        } else {
            plugin.getLogger().warning("ServerShop enabled but no servershop configs found - Unloading ServerShop");
            ServerShop.unload();
        }

        new ConfigReaderTask(plugin).runTaskLaterAsynchronously(plugin, 2*20);
    }

    public static CopyOnWriteArrayList<FileConfiguration> getAllConfigs() {
        return configs;
    }
}