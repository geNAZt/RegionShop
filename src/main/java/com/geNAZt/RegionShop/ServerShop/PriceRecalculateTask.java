package com.geNAZt.RegionShop.ServerShop;

import com.avaje.ebean.SqlRow;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.ItemName;
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
        ConcurrentHashMap<ItemStack, Price> currentPrices = PriceStorage.getAll();

        for(Map.Entry<ItemStack, Price> currentPrice : currentPrices.entrySet()) {
            Price price = currentPrice.getValue();

            SqlRow row = plugin.getDatabase().
                    createSqlQuery("SELECT AVG(`x`.`bought`) AS `bought`, AVG(`x`.`sold`) AS `sold` FROM (SELECT `bought`, `sold` FROM `shopserveritemaverage` WHERE `itemid` = :itemid AND `datavalue` = :datavalue ORDER BY `id` DESC LIMIT 60) x").
                    setParameter("itemid", currentPrice.getKey().getTypeId()).setParameter("datavalue", currentPrice.getKey().getData().getData()).findUnique();

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
                    plugin.getLogger().info("Recalc for Item: " + itemName);
                    plugin.getLogger().info("Avg. sold: " + sold);
                    plugin.getLogger().info("Avg. bought: " + bought);
                }

                Double sellPriceDiff = ((double) sold / (price.getMaxItemRecalc() / 60.0));
                Double buyPriceDiff;

                if(bought > 0) {
                    buyPriceDiff = ((price.getMaxItemRecalc() / 60.0) / (double) bought);
                } else {
                    buyPriceDiff = 2.0;
                }

                if(plugin.getConfig().getBoolean("debug")) {
                    plugin.getLogger().info("Calc. sellPriceDiff: "+ sellPriceDiff);
                    plugin.getLogger().info("Calc. buyPriceDiff: "+ buyPriceDiff);
                }

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

                if(plugin.getConfig().getBoolean("debug")) {
                    plugin.getLogger().info("Corr. sellPriceDiff: "+ sellPriceDiff);
                    plugin.getLogger().info("Corr. buyPriceDiff: "+ buyPriceDiff);
                }

                double newSellPrice = price.getSell();
                newSellPrice *= sellPriceDiff;

                double newBuyPrice = price.getBuy();
                newBuyPrice *= buyPriceDiff;

                if(plugin.getConfig().getBoolean("debug")) {
                    plugin.getLogger().info("New sellPrice: "+ newSellPrice);
                    plugin.getLogger().info("New buyPrice: "+ newBuyPrice);
                }

                price.setCurrentBuy(newBuyPrice);
                price.setCurrentSell(newSellPrice);
                PriceStorage.add(currentPrice.getKey(), price);
            }
        }
    }
}
