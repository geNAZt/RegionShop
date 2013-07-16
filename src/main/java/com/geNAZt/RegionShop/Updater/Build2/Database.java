package com.geNAZt.RegionShop.Updater.Build2;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlRow;
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
        EbeanServer dbServer = com.geNAZt.RegionShop.Database.Manager.createDatabaseServer(pl);

        SqlRow row = dbServer.createSqlQuery("ALTER TABLE `ShopServerItemAverage` ADD `region` VARCHAR(255);").findUnique();
        if(row.isEmpty()) {
            pl.getLogger().warning("Update to Build3 Database failed");
        }
    }
}
