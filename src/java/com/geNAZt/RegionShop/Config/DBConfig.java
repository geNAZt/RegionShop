package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.RegionShopPlugin;

import java.io.File;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class DBConfig extends Config {
    public DBConfig(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "db.yml");
        CONFIG_HEADER = new String[]{
            "This file holds all Config items for your Database Connection",
            "You need to change the url, username and password",
            "If you want to use another DB Driver you can use any other if they are supported in ebean",
            "The maxConnections only needs to be altered if you have huge database action"
        };
    }

    public String url = "jdbc:sqlite:{DIR}RegionShop.db";
    public String username = "walrus";
    public String password = "bukkit";
    public String driver = "org.sqlite.JDBC";
    public Integer maxConnections = 10;
}
