package com.geNAZt.RegionShop.Updater.Build3;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
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
        SpiEbeanServer serv = (SpiEbeanServer)dbServer;
        DdlGenerator gen = serv.getDdlGenerator();

        String createSQL = "create table ShopSellSign (\n" +
                "  id                        integer primary key,\n" +
                "  owner                     varchar(255),\n" +
                "  world                     varchar(255),\n" +
                "  shop                      varchar(255),\n" +
                "  x                         integer,\n" +
                "  y                         integer,\n" +
                "  z                         integer)\n" +
                ";";

        gen.runScript(false, createSQL);
    }
}
