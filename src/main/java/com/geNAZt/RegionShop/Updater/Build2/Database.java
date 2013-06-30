package com.geNAZt.RegionShop.Updater.Build2;

import com.avaje.ebean.EbeanServer;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Updater.Update;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 30.06.13
 */
public class Database extends Update {
    private RegionShopPlugin pl;

    public Database(RegionShopPlugin plugin) {
        pl = plugin;
    }

    @Override
    public void execute() {
        EbeanServer dbServer = com.geNAZt.RegionShop.Database.createDatabaseServer(pl);

        int row = dbServer.createSqlUpdate("ALTER TABLE `ShopServerItemAverage` ADD `region` VARCHAR(255);").execute();
    }
}
