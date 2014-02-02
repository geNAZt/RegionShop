package net.cubespace.RegionShop.Config.Files;


import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.Config;
import net.cubespace.RegionShop.Config.Files.Sub.Item;
import net.cubespace.RegionShop.Config.Files.Sub.ServerShop;

import java.io.File;
import java.util.ArrayList;

public class Servershop extends Config {
    public Servershop() {
        CONFIG_FILE = new File(Plugin.getInstance().getDataFolder() + File.separator + "config" + File.separator + "servershop.yml");
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
        item.limitSellPriceFactor = 5.0F;
        item.limitBuyPriceFactor = 2.0F;
        item.limitSellPriceUnderFactor = 0.6F;
        item.limitBuyPriceUnderFactor = 0.4F;

        ArrayList<Item> items = new ArrayList<Item>();
        items.add(item);

        ServerShop serverShop = new ServerShop();
        serverShop.Items = items;
        serverShop.Region = "TEST";

        ServerShops.add(serverShop);
    }

    public ArrayList<ServerShop> ServerShops = new ArrayList<ServerShop>();

    public ServerShop getServerShopByRegion(String region) {
        for(ServerShop serverShop : ServerShops) {
            if(serverShop.Region.toLowerCase().equals(region.toLowerCase())) {
                return serverShop;
            }
        }

        return null;
    }
}
