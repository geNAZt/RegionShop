package com.geNAZt.RegionShop.debugger;

import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 22.07.13
 */
public class Config {
    private static FileConfiguration config = null;

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void init(RegionShopPlugin plugin) {
        //Check if Config is given
        File devConfigFile = new File(plugin.getDataFolder(), "dev.yml");

        if(devConfigFile.exists()) {
            config = YamlConfiguration.loadConfiguration(devConfigFile);
        }
    }
}
