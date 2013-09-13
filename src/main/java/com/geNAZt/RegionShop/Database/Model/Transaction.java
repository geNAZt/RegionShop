package com.geNAZt.RegionShop.Database.Model;

import com.geNAZt.RegionShop.Database.Database;
import org.bukkit.OfflinePlayer;

import java.util.Date;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class Transaction {
    public static void generateTransaction(OfflinePlayer player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType type, String shop, String world, String owner, Integer item, Integer amount, Double sell, Double buy, Integer unitAmount) {
        com.geNAZt.RegionShop.Database.Table.Transaction shopTransaction = new com.geNAZt.RegionShop.Database.Table.Transaction();
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

        Database.getSaveQueue().add(shopTransaction);
    }
}
