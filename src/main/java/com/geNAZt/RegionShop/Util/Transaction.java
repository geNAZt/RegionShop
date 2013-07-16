package com.geNAZt.RegionShop.Util;

import com.geNAZt.RegionShop.Data.Queue.TransactionQueue;
import com.geNAZt.RegionShop.Data.Tasks.TransactionTask;
import com.geNAZt.RegionShop.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.OfflinePlayer;

import java.util.Date;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class Transaction {
    private static RegionShopPlugin plugin;

    public Transaction(RegionShopPlugin pl) {
        plugin = pl;

        if(plugin.getConfig().getBoolean("feature.transactions", true)) {
            new TransactionTask(plugin).runTaskTimerAsynchronously(plugin, 20, 5*20);
        }
    }

    public static void generateTransaction(OfflinePlayer player, ShopTransaction.TransactionType type, String shop, String world, String owner, Integer item, Integer amount, Double sell, Double buy, Integer unitAmount) {
        if(plugin.getConfig().getBoolean("feature.transactions", true)) {
            ShopTransaction shopTransaction = new ShopTransaction();
            shopTransaction.setIssuer(player.getName());
            shopTransaction.setType(type);
            shopTransaction.setShop(shop);
            shopTransaction.setOwner(owner);
            shopTransaction.setItem(item);
            shopTransaction.setAmount(amount);
            shopTransaction.setSell(sell);
            shopTransaction.setBuy(buy);
            shopTransaction.setDate(new Date());
            shopTransaction.setWorld(world);
            shopTransaction.setUnitAmount(unitAmount);

            TransactionQueue.addTransaction(shopTransaction);
        }
    }
}
