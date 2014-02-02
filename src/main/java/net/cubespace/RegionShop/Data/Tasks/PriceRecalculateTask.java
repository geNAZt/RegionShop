package net.cubespace.RegionShop.Data.Tasks;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Config.Files.Sub.Item;
import net.cubespace.RegionShop.Config.Files.Sub.ServerShop;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Table.CustomerSign;
import net.cubespace.RegionShop.Database.Table.ItemMeta;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Util.ItemName;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class PriceRecalculateTask extends BukkitRunnable {
    private HashMap<Integer, HashMap<String, ArrayList<Integer>>> recalcCache = new HashMap<Integer, HashMap<String, ArrayList<Integer>>>();

    private void prepareCache(Integer id) {
        if(!recalcCache.containsKey(id)) {
            HashMap<String, ArrayList<Integer>> newArrayList = new HashMap<String, ArrayList<Integer>>();
            newArrayList.put("buy", new ArrayList<Integer>(720));
            newArrayList.put("sell", new ArrayList<Integer>(720));

            for(Integer i = 0; i < 720; i++) {
                newArrayList.get("buy").add(0);
                newArrayList.get("sell").add(0);
            }

            recalcCache.put(id, newArrayList);
        }
    }

    private void addToCache(Integer id, Integer buy, Integer sell) {
        if(recalcCache.containsKey(id)) {
            HashMap<String, ArrayList<Integer>> newArrayList = recalcCache.get(id);

            if(newArrayList.get("buy").size() > 720) {
                newArrayList.get("buy").remove(0);
            }

            if(newArrayList.get("sell").size() > 720) {
                newArrayList.get("sell").remove(0);
            }

            newArrayList.get("sell").add(sell);
            newArrayList.get("buy").add(buy);
        }
    }

    private Integer getAverage(Integer id, String key) {
        if(recalcCache.containsKey(id)) {
            Integer amount = 0;
            ArrayList<Integer> newArrayList = recalcCache.get(id).get(key);

            for(Integer curAmount : newArrayList) {
                amount += curAmount;
            }

            return Math.round(amount / newArrayList.size());
        }

        return 0;
    }

    @Override
    public void run() {
        for (final ServerShop shop : ConfigManager.servershop.ServerShops) {
            Region region = null;
            try {
                region = Database.getDAO(Region.class).queryBuilder().where().eq("name", shop.Region).queryForFirst();
            } catch (SQLException e) {
                Logger.error("Could not get Region", e);
            }

            if(region == null) continue;

            for (Item item : shop.Items) {
                Items itemInShop = null;
                try {
                    ItemMeta itemMeta = Database.getDAO(ItemMeta.class).queryBuilder().
                            where().
                            eq("itemID", item.itemID).
                            and().
                            eq("dataValue", item.dataValue).queryForFirst();

                    if(itemMeta == null) continue;

                    itemInShop = Database.getDAO(Items.class).queryBuilder().
                            where().
                            eq("itemstorage_id", region.getItemStorage().getId()).
                            and().
                            eq("itemmeta_id", itemMeta.getId()).queryForFirst();
                } catch (SQLException e) {
                    Logger.error("Could not get Item", e);
                }

                if (itemInShop == null) continue;

                prepareCache(itemInShop.getId());

                Integer sold = (itemInShop.getSold()) * 720;
                Integer bought = (itemInShop.getBought()) * 720;

                addToCache(itemInShop.getId(), bought, sold);

                sold = getAverage(itemInShop.getId(), "sell");
                bought = getAverage(itemInShop.getId(), "buy");

                Float sellPriceDiff = (float) sold / item.maxItemRecalc;
                Float buyPriceDiff;

                if (bought > 0) {
                    buyPriceDiff = (float) item.maxItemRecalc / bought;
                } else {
                    buyPriceDiff = 2.0F;
                }

                if (sellPriceDiff > 1.0) {
                    //Preis geht rauf
                    if (sellPriceDiff > item.limitSellPriceFactor) {
                        sellPriceDiff = item.limitSellPriceFactor;
                    }
                } else {
                    //Preis geht runter
                    if (sellPriceDiff < item.limitSellPriceUnderFactor) {
                        sellPriceDiff = item.limitSellPriceUnderFactor;
                    }
                }

                if (buyPriceDiff > 1.0) {
                    //Abgabe geht rauf
                    buyPriceDiff = buyPriceDiff * item.limitBuyPriceFactor;
                } else {
                    //Abgabe geht runter
                    if (buyPriceDiff < item.limitBuyPriceUnderFactor) {
                        buyPriceDiff = item.limitBuyPriceUnderFactor;
                    }
                }

                Float newSellPrice = Math.round(item.sell * sellPriceDiff * 100) / 100.0F;
                Float newBuyPrice = Math.round(item.buy * buyPriceDiff * 100) / 100.0F;

                itemInShop.setBuy(newBuyPrice);
                itemInShop.setSell(newSellPrice);
                itemInShop.setCurrentAmount(99999);
                itemInShop.setBought(0);
                itemInShop.setSold(0);

                try {
                    Database.getDAO(Items.class).update(itemInShop);
                } catch (SQLException e) {
                    Logger.error("Could not update Item", e);
                }

                //Check if Item has a Sign
                CustomerSign customerSign = null;
                try {
                    customerSign = Database.getDAO(CustomerSign.class).queryBuilder().
                            where().
                            eq("item_id", itemInShop.getId()).
                            queryForFirst();
                } catch (SQLException e) {
                    Logger.error("Could not get Customer Sign", e);
                }

                final Items items = itemInShop;

                if (customerSign != null) {
                    final CustomerSign syncCustomerSign = customerSign;
                    Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            Block block = Plugin.getInstance().getServer().getWorld(syncCustomerSign.getRegion().getWorld()).getBlockAt(syncCustomerSign.getX(), syncCustomerSign.getY(), syncCustomerSign.getZ());
                            if (block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN)) {
                                Sign sign = (Sign) block.getState();

                                //Get the nice name
                                ItemStack itemStack = ItemRepository.fromDBItem(items);

                                String dataName = ItemName.getDataName(itemStack);
                                String niceItemName;
                                if(dataName.endsWith(" ")) {
                                    niceItemName = dataName + ItemName.nicer(itemStack.getType().toString());
                                } else if(!dataName.equals("")) {
                                    niceItemName = dataName;
                                } else {
                                    niceItemName = ItemName.nicer(itemStack.getType().toString());
                                }

                                if (itemStack.getItemMeta().hasDisplayName()) {
                                    niceItemName = "(" + itemStack.getItemMeta().getDisplayName() + ")";
                                }

                                for (Integer line = 0; line < 4; line++) {
                                    sign.setLine(line, ConfigManager.language.Sign_Customer_SignText.get(line).
                                            replace("%id", items.getId().toString()).
                                            replace("%itemname", ItemName.nicer(niceItemName)).
                                            replace("%amount", items.getUnitAmount().toString()).
                                            replace("%sell", items.getSell().toString()).
                                            replace("%buy", items.getBuy().toString()));
                                }

                                sign.update();
                            }
                        }
                    });
                }
            }

            //Reset the ItemStorage to avoid "Shop is full you cant sell"
            try {
                ItemStorage itemStorage = Database.getDAO(ItemStorage.class).queryBuilder().where().eq("id", region.getItemStorage().getId()).queryForFirst();
                itemStorage.setItemAmount(0);
                Database.getDAO(ItemStorage.class).update(itemStorage);
            } catch (SQLException e) {
                Logger.error("Could not reset ItemStorage", e);
            }
        }
    }
}

