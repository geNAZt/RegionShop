package com.geNAZt.RegionShop.ServerShop;

import com.geNAZt.RegionShop.Model.ShopServerItemAverage;
import com.geNAZt.RegionShop.RegionShopPlugin;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class ItemAverageTask extends BukkitRunnable {
    private RegionShopPlugin plugin;

    public ItemAverageTask(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        HashMap<ItemStack, Price> currentPrices = PriceStorage.getAll();

        for(Map.Entry<ItemStack, Price> currentPrice : currentPrices.entrySet()) {
            Price price = currentPrice.getValue();

            ShopServerItemAverage item = new ShopServerItemAverage();
            item.setSold(price.getSold());
            item.setBought(price.getBought());
            item.setDatavalue(currentPrice.getKey().getData().getData());
            item.setItemid(currentPrice.getKey().getTypeId());
            item.setDate(new Date());
            plugin.getDatabase().save(item);

            price.setBought(0);
            price.setSold(0);

            PriceStorage.add(currentPrice.getKey(), price);
        }
    }
}
