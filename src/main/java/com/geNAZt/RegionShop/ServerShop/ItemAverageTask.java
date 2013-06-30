package com.geNAZt.RegionShop.ServerShop;

import com.geNAZt.RegionShop.Model.ShopServerItemAverage;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
class ItemAverageTask extends BukkitRunnable {
    private final RegionShopPlugin plugin;

    public ItemAverageTask(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, Price>> currentPrices = PriceStorage.getAll();

        for(Map.Entry<String, ConcurrentHashMap<ItemStack, Price>> currentRegion : currentPrices.entrySet()) {
            for(Map.Entry<ItemStack, Price> currentPrice : currentRegion.getValue().entrySet()) {
                Price price = currentPrice.getValue();

                ShopServerItemAverage item = new ShopServerItemAverage();
                item.setSold(price.getSold());
                item.setBought(price.getBought());
                item.setDatavalue(currentPrice.getKey().getData().getData());
                item.setItemid(currentPrice.getKey().getTypeId());
                item.setDate(new Date());
                item.setRegion(currentRegion.getKey());

                plugin.getDatabase().save(item);

                price.setBought(0);
                price.setSold(0);

                PriceStorage.add(currentRegion.getKey(), currentPrice.getKey(), price);
            }
        }
    }
}
