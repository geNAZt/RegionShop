package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Storages.Profiler;
import com.geNAZt.RegionShop.Data.Struct.Price;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.ServerShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 18.06.13
 */
public class ConfigReaderTask extends BukkitRunnable {
    private final RegionShopPlugin plugin;

    public ConfigReaderTask(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    private <T> T autoCast(Object obj) {
        return (T) obj;
    }

    @Override
    public void run() {
        Profiler.start("ConfigReaderTask");

        plugin.getLogger().info("=== Loading ServerShops ===");

        CopyOnWriteArrayList<FileConfiguration> configs = ServerShop.getAllConfigs();

        for(FileConfiguration config : configs) {
            List<Map<?, ?>> list = config.getMapList("items");

            String region;
            if(config.get("region") != null) {
                plugin.getLogger().info("ServerShop for Region: " + config.get("region"));
                region = config.getString("region");
            } else {
                plugin.getLogger().info("ServeShop Global");
                region = "GLOBAL";
            }

            for(Map<?, ?> section : list) {
                Map<String, Object> item = new HashMap<String, Object>();

                for(Map.Entry<?, ?> sectionEntry : section.entrySet()) {
                    item.put((String)sectionEntry.getKey(), sectionEntry.getValue());
                }

                Price price = new Price();
                price.setBuy((Double)autoCast(item.get("buy")));
                price.setSell((Double) autoCast(item.get("sell")));
                price.setMaxItemRecalc((Integer) autoCast(item.get("maxItemRecalc")));
                price.setLimitSellPriceFactor((Double) autoCast(item.get("limitSellPriceFactor")));
                price.setLimitBuyPriceFactor((Double) autoCast(item.get("limitBuyPriceFactor")));
                price.setLimitSellPriceUnderFactor((Double) autoCast(item.get("limitSellPriceUnderFactor")));
                price.setLimitBuyPriceUnderFactor((Double) autoCast(item.get("limitBuyPriceUnderFactor")));

                ItemStack itemStack = new ItemStack((Integer) autoCast(item.get("itemid")), 1);
                Integer dataValue = autoCast(item.get("datavalue"));
                if(dataValue != 0) {
                    itemStack.getData().setData(dataValue.byteValue());
                }

                PriceStorage.add(region, itemStack, price);

                plugin.getLogger().info("Found ItemID: " + itemStack);
            }
        }

        ServerShop.itemAverageTask = new ItemAverageTask(plugin).runTaskTimerAsynchronously(plugin, 60*20, 60*20);
        ServerShop.priceRecalculateTask = new PriceRecalculateTask(plugin).runTaskTimerAsynchronously(plugin, 20, 300*20);

        Profiler.end("ConfigReaderTask");
    }
}
