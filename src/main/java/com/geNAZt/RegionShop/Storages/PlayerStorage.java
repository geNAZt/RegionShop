package com.geNAZt.RegionShop.Storages;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class PlayerStorage {
    private static HashMap<Player, String> playersInRegions = new HashMap<Player, String>();

    public static String getPlayer(Player plyr) {
        if (!playersInRegions.containsKey(plyr)) {
            return null;
        }

        return playersInRegions.get(plyr);
    }

    public static void setPlayer(Player plyr, String region) {
        playersInRegions.put(plyr, region);
    }

    public static void removerPlayer(Player plyr) {
        playersInRegions.remove(plyr);
    }
}
