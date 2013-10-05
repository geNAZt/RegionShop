package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.ServerItemAverage;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class ItemAverageTask extends BukkitRunnable {
    @Override
    public void run() {
        List<Items> itemsList = Database.getServer().find(Items.class).findList();

        for(Items items : itemsList) {
            if(!items.getItemStorage().getRegions().iterator().hasNext()) continue;
            if(!items.getItemStorage().isServershop()) continue;

            ServerItemAverage item = new ServerItemAverage();
            item.setSold(items.getSold());
            item.setBought(items.getBought());
            item.setMeta(items.getMeta());
            item.setDate(new Date());
            item.setRegion(items.getItemStorage().getRegions().iterator().next());
            item.setServershop(items.getItemStorage().isServershop());

            Database.getServer().save(item);

            items.setCurrentAmount(99999);

            items.setBought(0);
            items.setSold(0);

            Database.getServer().update(items);
        }
    }
}
