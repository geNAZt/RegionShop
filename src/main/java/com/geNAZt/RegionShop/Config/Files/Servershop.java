package com.geNAZt.RegionShop.Config.Files;

import com.geNAZt.RegionShop.Config.Config;
import com.geNAZt.RegionShop.Config.Sub.Item;
import com.geNAZt.RegionShop.Config.Sub.ServerShop;
import com.geNAZt.RegionShop.RegionShopPlugin;

import java.io.File;
import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.09.13
 */
public class Servershop extends Config {
    public Servershop(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "servershop.yml");
        CONFIG_HEADER = new String[]{
                "This file holds all ServerShops",
                "For the documentation about the Config Settings visit https://github.com/geNAZt/RegionShop/wiki"
        };

        Item item = new Item();
        item.itemID = 1;
        item.dataValue = 0;
        item.buy = 0.5F;
        item.sell = 1.0F;
        item.maxItemRecalc = 50;
        item.limitSellPriceFactor = 5.0;
        item.limitBuyPriceFactor = 2.0;
        item.limitSellPriceUnderFactor = 0.6;
        item.limitBuyPriceUnderFactor = 0.4;

        ArrayList<Item> items = new ArrayList<Item>();
        items.add(item);

        ServerShop serverShop = new ServerShop();
        serverShop.Items = items;
        serverShop.Region = "TEST";

        ServerShops.add(serverShop);
    }

    public ArrayList<ServerShop> ServerShops = new ArrayList<ServerShop>();
}
