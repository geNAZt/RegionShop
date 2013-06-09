package com.geNAZt.RegionShop.Storages;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class PlayerStorage {
    private static final HashMap<Player, String> playersInRegions = new HashMap<Player, String>();

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
