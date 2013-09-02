package com.geNAZt.RegionShop.Data.Tasks;

import com.avaje.ebean.SqlRow;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.Price;
import com.geNAZt.RegionShop.Database.Database.Model.ShopCustomerSign;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.debugger.Profiler.Profiler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
class PriceRecalculateTask extends BukkitRunnable {
    private final RegionShopPlugin plugin;

    public PriceRecalculateTask(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {
        Profiler.start("PriceRecalculateTask");

        ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, Price>> currentPrices = PriceStorage.getAll();

        for(Map.Entry<String, ConcurrentHashMap<ItemStack, Price>> currentServerShop : currentPrices.entrySet()) {
            for(Map.Entry<ItemStack, Price> currentPrice : currentServerShop.getValue().entrySet()) {
                Price price = currentPrice.getValue();

                SqlRow row = plugin.getDatabase().
                        createSqlQuery("SELECT AVG(`x`.`bought`) AS `bought`, AVG(`x`.`sold`) AS `sold` FROM (SELECT `bought`, `sold` FROM `shopserveritemaverage` WHERE `itemid` = :itemid AND `datavalue` = :datavalue AND `region` = :region ORDER BY `id` DESC LIMIT 60) x").
                        setParameter("itemid", currentPrice.getKey().getTypeId()).setParameter("datavalue", currentPrice.getKey().getData().getData()).setParameter("region", currentServerShop.getKey()).findUnique();

                if(!row.isEmpty()) {
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

                    if(plugin.getConfig().getBoolean("debug")) {
                        String niceItemName = ItemName.nicer(currentPrice.getKey().getType().toString());
                        String itemName = ItemName.getDataName(currentPrice.getKey()) + niceItemName;
                        Logger.debug("Recalc for Item: " + itemName);
                        Logger.debug("Avg. sold: " + sold);
                        Logger.debug("Avg. bought: " + bought);
                    }

                    Double sellPriceDiff = ((double) sold / (price.getMaxItemRecalc() / 60.0));
                    Double buyPriceDiff;

                    if(bought > 0) {
                        buyPriceDiff = ((price.getMaxItemRecalc() / 60.0) / (double) bought);
                    } else {
                        buyPriceDiff = 2.0;
                    }

                    Logger.debug("Calc. sellPriceDiff: "+ sellPriceDiff);
                    Logger.debug("Calc. buyPriceDiff: "+ buyPriceDiff);

                    if(sellPriceDiff > 1.0) {
                        //Preis geht rauf
                        if(sellPriceDiff > price.getLimitSellPriceFactor()) {
                            sellPriceDiff = price.getLimitSellPriceFactor();
                        }
                    } else {
                        //Preis geht runter
                        if(sellPriceDiff < price.getLimitSellPriceUnderFactor()) {
                            sellPriceDiff = price.getLimitSellPriceUnderFactor();
                        }
                    }

                    if(buyPriceDiff > 1.0) {
                        //Abgabe geht rauf
                        buyPriceDiff = buyPriceDiff * price.getLimitBuyPriceFactor();
                    } else {
                        //Abgabe geht runter
                        if(buyPriceDiff < price.getLimitBuyPriceUnderFactor()) {
                            buyPriceDiff = price.getLimitBuyPriceUnderFactor();
                        }
                    }

                    Logger.debug("Corr. sellPriceDiff: "+ sellPriceDiff);
                    Logger.debug("Corr. buyPriceDiff: "+ buyPriceDiff);

                    double newSellPrice = price.getSell();
                    newSellPrice *= sellPriceDiff;

                    double newBuyPrice = price.getBuy();
                    newBuyPrice *= buyPriceDiff;

                    Logger.debug("New sellPrice: "+ newSellPrice);
                    Logger.debug("New buyPrice: "+ newBuyPrice);

                    price.setCurrentBuy(newBuyPrice);
                    price.setCurrentSell(newSellPrice);
                    PriceStorage.add(currentServerShop.getKey(), currentPrice.getKey(), price);

                    //Check if Item has a Sign
                    final ShopCustomerSign customerSign = plugin.getDatabase().find(ShopCustomerSign.class).
                            where().
                                conjunction().
                                    eq("shop", currentServerShop.getKey()).
                                    eq("itemid", currentPrice.getKey().getTypeId()).
                                    eq("datavalue", currentPrice.getKey().getData().getData()).
                                    eq("is_servershop", 1).
                                endJunction().
                            findUnique();

                    final double newSellPriceRead = newSellPrice;
                    final double newBuyPriceRead = newBuyPrice;

                    if(customerSign != null) {
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                Block block = plugin.getServer().getWorld(customerSign.getWorld()).getBlockAt(customerSign.getX(), customerSign.getY(), customerSign.getZ());
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

        Profiler.end("PriceRecalculateTask");
    }
}
