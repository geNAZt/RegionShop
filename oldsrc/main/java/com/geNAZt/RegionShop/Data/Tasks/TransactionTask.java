package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Data.Queue.TransactionQueue;
import com.geNAZt.RegionShop.Database.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class TransactionTask extends BukkitRunnable {
    private final RegionShopPlugin plugin;

    public TransactionTask(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ShopTransaction shopTransaction = TransactionQueue.getTransaction();

        while(shopTransaction != null) {
            plugin.getDatabase().save(shopTransaction);
            shopTransaction = TransactionQueue.getTransaction();
        }
    }
}
