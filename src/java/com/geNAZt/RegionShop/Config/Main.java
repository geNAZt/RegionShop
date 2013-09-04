package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.RegionShopPlugin;

import java.io.File;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class Main extends Config {
    public Main(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "main.yml");
        CONFIG_HEADER = new String[]{
            "This file holds the Settings that should be changed.",
            "For the documentation about the Config Settings visit https://github.com/geNAZt/RegionShop/wiki"
        };
    }

    public String DB_url = "jdbc:sqlite:{DIR}RegionShop.db";
    public String DB_username = "walrus";
    public String DB_password = "bukkit";
    public String DB_driver = "org.sqlite.JDBC";
    public String Chat_prefix = "[RS] ";
}
