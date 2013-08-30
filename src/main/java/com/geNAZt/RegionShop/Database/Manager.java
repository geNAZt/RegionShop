package com.geNAZt.RegionShop.Database;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.LogLevel;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.geNAZt.RegionShop.RegionShopPlugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 26.06.13
 */
public class Manager {
    public static EbeanServer createDatabaseServer(RegionShopPlugin plugin) {
        ServerConfig db = new ServerConfig();
        db.setDefaultServer(false);
        db.setRegister(false);
        db.setClasses(plugin.getDatabaseClasses());
        db.setName("RegionShop");
        db.setLoggingLevel(LogLevel.NONE);

        DataSourceConfig ds = new DataSourceConfig();
        ds.setDriver(plugin.getConfig().getString("database.driver"));
        ds.setUrl(plugin.getConfig().getString("database.url").replaceAll("\\{DIR\\}", plugin.getDataFolder().getPath().replaceAll("\\\\", "/") + "/"));
        ds.setUsername(plugin.getConfig().getString("database.username"));
        ds.setPassword(plugin.getConfig().getString("database.password"));
        ds.setIsolationLevel(TransactionIsolation.getLevel(plugin.getConfig().getString("database.isolation")));
        ds.setMaxConnections(plugin.getConfig().getInt("database.maxConnections"));

        if (ds.getDriver().contains("sqlite")) {
            db.setDatabasePlatform(new SQLitePlatform());
            db.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        db.setDataSourceConfig(ds);

        ClassLoader previous = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(Manager.class.getClassLoader());
        EbeanServer dbs = EbeanServerFactory.create(db);
        Thread.currentThread().setContextClassLoader(previous);

        return dbs;
    }
}
