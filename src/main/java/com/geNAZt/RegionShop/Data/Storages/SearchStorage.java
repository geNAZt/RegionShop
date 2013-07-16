package com.geNAZt.RegionShop.Data.Storages;

import com.geNAZt.RegionShop.Database.Model.ShopItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class SearchStorage {
    private static class SearchItem {
        public Player plyr;
        public String searchQry;
    }

    private static final ConcurrentHashMap<SearchItem, ConcurrentHashMap<ShopItems, ItemStack>> playerSearchQuery = new ConcurrentHashMap<SearchItem, ConcurrentHashMap<ShopItems, ItemStack>>();

    public static boolean hasPlayer(Player plyr) {
        for(Map.Entry<SearchItem, ConcurrentHashMap<ShopItems, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return true;
            }
        }

        return false;
    }

    public static void removeAllPlayer(Player plyr) {
        for(Map.Entry<SearchItem, ConcurrentHashMap<ShopItems, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                playerSearchQuery.remove(item.getKey());
            }
        }
    }

    public static String getSearchQuery(Player plyr) {
        for(Map.Entry<SearchItem, ConcurrentHashMap<ShopItems, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return item.getKey().searchQry;
            }
        }

        return null;
    }

    public static ConcurrentHashMap<ShopItems, ItemStack> getSearchResult(Player plyr) {
        for(Map.Entry<SearchItem, ConcurrentHashMap<ShopItems, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return item.getValue();
            }
        }

        return null;
    }

    public static void putSearchResults(Player plyr, String searchQry, ConcurrentHashMap<ShopItems, ItemStack> result) {
        SearchItem srchItm = new SearchItem();
        srchItm.plyr = plyr;
        srchItm.searchQry = searchQry;

        playerSearchQuery.put(srchItm, result);
    }
}
