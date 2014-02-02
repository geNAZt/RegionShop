package net.cubespace.RegionShop.Database.Repository;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Transaction;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.Date;

public class TransactionRepository {
    public static void generateTransaction(OfflinePlayer player, Transaction.TransactionType type, String shop, String world, String owner, Integer item, Integer amount, Double sell, Double buy, Integer unitAmount) {
        Transaction shopTransaction = new Transaction();
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

        try {
            Database.getDAO(Transaction.class).create(shopTransaction);
        } catch (SQLException e) {
            Plugin.getInstance().getLogger().warning("Could not create Transaction");
            e.printStackTrace();
        }
    }
}
