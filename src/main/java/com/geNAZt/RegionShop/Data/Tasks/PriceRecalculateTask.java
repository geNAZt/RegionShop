package com.geNAZt.RegionShop.Data.Tasks;

import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Config.Sub.Item;
import com.geNAZt.RegionShop.Config.Sub.ServerShop;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.CustomerSign;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class PriceRecalculateTask extends BukkitRunnable {
    @Override
    public void run() {
        for(ServerShop shop : ConfigManager.servershop.ServerShops) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -30);

            for(Item item : shop.Items) {
                SqlQuery query = Database.getServer().
                    createSqlQuery(
                            "SELECT  AVG(`x`.`bought`) AS `bought`, " +
                                    "AVG(`x`.`sold`)   AS `sold` " +
                                    "FROM (SELECT " +
                                    "`bought`," +
                                    "`sold`" +
                                    "FROM `rs_itemaverage` LEFT JOIN `rs_region` ON `region_id` == `rs_region`.`id`" +
                                    "WHERE `item_id` = :itemid AND `data_value` = :datavalue AND `date` > :date AND `rs_region`.`region` = :region ORDER BY `rs_itemaverage`.`id` DESC) x").
                            setParameter("itemid", item.itemID).
                            setParameter("datavalue", item.dataValue).
                            setParameter("date", calendar.getTime().getTime()).
                            setParameter("region", shop.Region);

                SqlRow row = query.findUnique();

                if(!row.isEmpty()) {
                    Items itemInShop = Database.getServer().find(Items.class).
                            where().
                                eq("meta.id.itemID", item.itemID).
                                eq("meta.id.dataValue", item.dataValue).
                                eq("itemStorage.regions.region", shop.Region).
                            findUnique();

                    Double soldDec = (Double) row.get("sold");
                    Double boughtDec = (Double) row.get("bought");

                    Integer sold = 0;
                    Integer bought = 0;

                    if(soldDec != null) {
                        sold = soldDec.intValue();
                    }

                    if(boughtDec != null) {
                        bought = boughtDec.intValue();
                    }

                    Float sellPriceDiff = (sold / (item.maxItemRecalc / 30.0F));
                    Float buyPriceDiff;

                    if(bought > 0) {
                        buyPriceDiff = ((item.maxItemRecalc / 30.0F) / bought);
                    } else {
                        buyPriceDiff = 2.0F;
                    }

                    Logger.debug("Calc. sellPriceDiff: " + sellPriceDiff);
                    Logger.debug("Calc. buyPriceDiff: "+ buyPriceDiff);

                    if(sellPriceDiff > 1.0) {
                        //Preis geht rauf
                        if(sellPriceDiff > item.limitSellPriceFactor) {
                            sellPriceDiff = item.limitSellPriceFactor;
                        }
                    } else {
                        //Preis geht runter
                        if(sellPriceDiff < item.limitSellPriceUnderFactor) {
                            sellPriceDiff = item.limitSellPriceUnderFactor;
                        }
                    }

                    if(buyPriceDiff > 1.0) {
                        //Abgabe geht rauf
                        buyPriceDiff = buyPriceDiff * item.limitBuyPriceFactor;
                    } else {
                        //Abgabe geht runter
                        if(buyPriceDiff < item.limitBuyPriceUnderFactor) {
                            buyPriceDiff = item.limitBuyPriceUnderFactor;
                        }
                    }

                    Logger.debug("Corr. sellPriceDiff: "+ sellPriceDiff);
                    Logger.debug("Corr. buyPriceDiff: "+ buyPriceDiff);

                    Float newSellPrice = item.sell;
                    newSellPrice *= sellPriceDiff;

                    Float newBuyPrice = item.buy;
                    newBuyPrice *= buyPriceDiff;

                    Logger.debug("New sellPrice: "+ newSellPrice);
                    Logger.debug("New buyPrice: "+ newBuyPrice);

                    itemInShop.setBuy(newBuyPrice);
                    itemInShop.setSell(newSellPrice);

                    Database.getServer().update(itemInShop);

                    //Check if Item has a Sign
                    final CustomerSign customerSign = Database.getServer().find(CustomerSign.class).
                            where().
                                conjunction().
                                    eq("item", itemInShop).
                                endJunction().
                            findUnique();

                    final double newSellPriceRead = newSellPrice;
                    final double newBuyPriceRead = newBuyPrice;

                    if(customerSign != null) {
                        RegionShopPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(RegionShopPlugin.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                Block block = RegionShopPlugin.getInstance().getServer().getWorld(customerSign.getRegion().getWorld()).getBlockAt(customerSign.getX(), customerSign.getY(), customerSign.getZ());
                                if(block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN)) {
                                    Sign sign = (Sign)block.getState();

                                    sign.setLine(2, "B " + newSellPriceRead + "$:S " + newBuyPriceRead + "$");
                                    sign.update();
                                }
                            }
                        });

                    }
                }
            }
        }
    }
}
