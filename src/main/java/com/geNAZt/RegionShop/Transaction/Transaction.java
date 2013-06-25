package com.geNAZt.RegionShop.Transaction;

import com.geNAZt.RegionShop.Model.ShopTransaction;
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

    public static void generateTransaction(OfflinePlayer player, ShopTransaction.TransactionType type, String shop, String owner, Integer item, Integer amount, Double sell, Double buy) {
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

            TransactionQueue.addTransaction(shopTransaction);
        }
    }
}
