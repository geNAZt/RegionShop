package com.geNAZt.RegionShop.Storages;

import com.geNAZt.RegionShop.Model.ShopItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class SearchStorage {
    private static class SearchItem {
        public Player plyr;
        public String searchQry;
    }

    private static HashMap<SearchItem, HashMap<ShopItems, ItemStack>> playerSearchQuery = new HashMap<SearchItem, HashMap<ShopItems, ItemStack>>();

    public static boolean hasPlayer(Player plyr) {
        for(Map.Entry<SearchItem, HashMap<ShopItems, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return true;
            }
        }

        return false;
    }

    public static void removeAllPlayer(Player plyr) {
        for(Map.Entry<SearchItem, HashMap<ShopItems, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                playerSearchQuery.remove(item.getKey());
            }
        }
    }

    public static String getSearchQuery(Player plyr) {
        for(Map.Entry<SearchItem, HashMap<ShopItems, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return item.getKey().searchQry;
            }
        }

        return null;
    }

    public static HashMap<ShopItems, ItemStack> getSearchResult(Player plyr) {
        for(Map.Entry<SearchItem, HashMap<ShopItems, ItemStack>> item : playerSearchQuery.entrySet()) {
            if(item.getKey().plyr == plyr) {
                return item.getValue();
            }
        }

        return null;
    }

    public static void putSearchResults(Player plyr, String searchQry, HashMap<ShopItems, ItemStack> result) {
        SearchItem srchItm = new SearchItem();
        srchItm.plyr = plyr;
        srchItm.searchQry = searchQry;

        playerSearchQuery.put(srchItm, result);
    }
}
