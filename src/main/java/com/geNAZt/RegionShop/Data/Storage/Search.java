package com.geNAZt.RegionShop.Data.Storage;

import com.geNAZt.RegionShop.Database.Table.Items;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Search {
    private static class SearchItem {
        public Player plyr;
        public String searchQry;
    }

    private static final ConcurrentHashMap<SearchItem, ConcurrentHashMap<Items, ItemStack>> playerSearchQuery = new ConcurrentHashMap<SearchItem, ConcurrentHashMap<Items, ItemStack>>();

    public static boolean has(Player plyr) {
        for(Map.Entry<SearchItem, ConcurrentHashMap<Items, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return true;
            }
        }

        return false;
    }

    public static void remove(Player plyr) {
        for(Map.Entry<SearchItem, ConcurrentHashMap<Items, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                playerSearchQuery.remove(item.getKey());
            }
        }
    }

    public static String getSearchQuery(Player plyr) {
        for(Map.Entry<SearchItem, ConcurrentHashMap<Items, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return item.getKey().searchQry;
            }
        }

        return null;
    }

    public static ConcurrentHashMap<Items, ItemStack> getSearchResult(Player plyr) {
        for(Map.Entry<SearchItem, ConcurrentHashMap<Items, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return item.getValue();
            }
        }

        return null;
    }

    public static void put(Player plyr, String searchQry, ConcurrentHashMap<Items, ItemStack> result) {
        SearchItem srchItm = new SearchItem();
        srchItm.plyr = plyr;
        srchItm.searchQry = searchQry;

        playerSearchQuery.put(srchItm, result);
    }
}
